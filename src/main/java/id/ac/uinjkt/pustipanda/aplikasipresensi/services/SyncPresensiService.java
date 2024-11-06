package id.ac.uinjkt.pustipanda.aplikasipresensi.services;

import id.ac.uinjkt.pustipanda.aplikasipresensi.constant.AppConstant;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Absensi;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.presensi.Pegawai;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms.IclockTransaction;
import id.ac.uinjkt.pustipanda.aplikasipresensi.entity.wdms.TransactionPresensi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class SyncPresensiService {
    @Autowired
    private TransactionPresensiService transactionPresensiService;

    @Autowired
    private IclockTransactionService iclockTransactionService;

    @Autowired
    private PegawaiService pegawaiService;

    @Autowired
    private AbsensiService absensiService;

    //@Scheduled(fixedDelay = 5000)
    @Scheduled(cron = "0 */5 * ? * *")
    public void syncTerminaltoPresensi() {

        log.info("Current time is :: {}", LocalDateTime.now());
        // baca data
        List<TransactionPresensi> datas = transactionPresensiService.findAll();
        //datas.forEach(System.out::println);

        if (datas.size() > 0) {
            // filter data yg in / out by pegawai
            List<Pegawai> listPegawai = pegawaiService.findAllByStatusAktif();
            for (Pegawai p : listPegawai) {
                //for IN -> 0, 3, 4
                List<TransactionPresensi> dataFilterPegawaiIn = datas
                        .stream().
                        filter(transactionPresensi -> transactionPresensi.getPegawai().equals(p.getId())
                                && (transactionPresensi.getInOut() == AppConstant.TRANSACTION_PRESENSI_CHECK_IN
                                || transactionPresensi.getInOut() == AppConstant.TRANSACTION_PRESENSI_BREAK_IN
                                || transactionPresensi.getInOut() == AppConstant.TRANSACTION_PRESENSI_OVERTIME_IN))
                        .sorted(Comparator.comparing(TransactionPresensi::getIclockTransaction))
                        .collect(toList());

                if (dataFilterPegawaiIn.size() > 0) {
                    Map<LocalDate, List<TransactionPresensi>> groupedByDateIn = dataFilterPegawaiIn.stream()
                            .collect(Collectors.groupingBy(TransactionPresensi::getDateDate, LinkedHashMap::new, toList()));

                    for (List<TransactionPresensi> transactionPresensis : groupedByDateIn.values()) {
                        int i = 0;
                        for (TransactionPresensi transactionPresensi : transactionPresensis) {
                            Absensi absensi = new Absensi();
                            if (i == 0) {
                                // if in, waktu awal yg dipakai
                                absensi = Optional.ofNullable(absensiService.getAbsensi(p, transactionPresensi.getDateDate()))
                                        .orElse(new Absensi(p, transactionPresensi.getDateDate()));
                                if (absensi.getWaktuIn() == null) {
                                    // save ke presensi sesuai dgn in pegawai
                                    absensi = absensiService.setDataWaktuIn(absensi, transactionPresensi.getDateDate(), transactionPresensi.getDateTime());
                                    absensiService.save(absensi);
                                    log.info("save by snyc to tabel absensi IN pegawai " + absensi.getPegawai().getId());

                                    // update ke TransactionPresensi is_sync=true
                                    if (!transactionPresensi.getIsSync()) {
                                        Optional<IclockTransaction> iclockTransaction = iclockTransactionService.findById(transactionPresensi.getIclockTransaction());
                                        if (iclockTransaction.isPresent()) {
                                            //epEptransaction.get().setTemperatureF(BigDecimal.valueOf(0));
                                            iclockTransaction.get().setIsSync(true);
                                            iclockTransactionService.save(iclockTransaction.get());
                                            log.info("update to TRUE by sync IN transactionpresensi id " + transactionPresensi.getIclockTransaction());
                                        }
                                    }
                                }
                            }

                            if (absensi.getId() != null) {
                                // if in, ada banyak data & di presensi sudah ke save, maka data lainnya di is_sync=true
                                if (!transactionPresensi.getIsSync()) {
                                    Optional<IclockTransaction> iclockTransaction = iclockTransactionService.findById(transactionPresensi.getIclockTransaction());
                                    if (iclockTransaction.isPresent()) {
                                        //epEptransaction.get().setTemperatureF(BigDecimal.valueOf(0));
                                        iclockTransaction.get().setIsSync(true);
                                        iclockTransactionService.save(iclockTransaction.get());
                                        log.info("update data lainnya to TRUE by sync IN transactionpresensi id " + transactionPresensi.getIclockTransaction());
                                    }
                                }
                            }
                            i++;
                        }
                    }
                }

                //for OUT -> 1, 2, 5
                List<TransactionPresensi> dataFilterPegawaiOut = datas.stream().
                        filter(transactionPresensi -> transactionPresensi.getPegawai().equals(p.getId())
                                && (transactionPresensi.getInOut() == AppConstant.TRANSACTION_PRESENSI_CHECK_OUT
                                || transactionPresensi.getInOut() == AppConstant.TRANSACTION_PRESENSI_BREAK_OUT
                                || transactionPresensi.getInOut() == AppConstant.TRANSACTION_PRESENSI_OVERTIME_OUT))
                        .sorted(Comparator.comparing(TransactionPresensi::getIclockTransaction))
                        .collect(toList());

                if (dataFilterPegawaiOut.size() > 0) {
                    Map<LocalDate, List<TransactionPresensi>> groupedByDateOut = dataFilterPegawaiOut.stream()
                            .collect(Collectors.groupingBy(TransactionPresensi::getDateDate, LinkedHashMap::new, toList()));

                    for (List<TransactionPresensi> transactionPresensis : groupedByDateOut.values()) {
                        int i = 0;
                        for (TransactionPresensi transactionPresensi : transactionPresensis) {
                            Absensi absensi = new Absensi();
                            if ((transactionPresensis.size() - 1) == i) {
                                // if out, waktu yg akhir yg di pakai (update)
                                absensi = Optional.ofNullable(absensiService.getAbsensi(p, transactionPresensi.getDateDate()))
                                        .orElse(new Absensi(p, transactionPresensi.getDateDate()));
                                // save ke presensi sesuai dgn out pegawai
                                absensi = absensiService.setDataWaktuOut(absensi, transactionPresensi.getDateDate(), transactionPresensi.getDateTime());
                                absensiService.save(absensi);
                                log.info("save by snyc to tabel absensi OUT pegawai " + absensi.getPegawai().getId());

                                // update ke TransactionPresensi is_sync=true
                                if (!transactionPresensi.getIsSync()) {
                                    Optional<IclockTransaction> iclockTransaction = iclockTransactionService.findById(transactionPresensi.getIclockTransaction());
                                    if (iclockTransaction.isPresent()) {
                                        //epEptransaction.get().setTemperatureF(BigDecimal.valueOf(0));
                                        iclockTransaction.get().setIsSync(true);
                                        iclockTransactionService.save(iclockTransaction.get());
                                        log.info("update to TRUE by sync OUT transactionpresensi id " + transactionPresensi.getIclockTransaction());
                                    }
                                }
                            }

                            if (i == 0) {
                                //  if out, data ada banyak, data awalnya di is_sync=true
                                if (!transactionPresensi.getIsSync()) {
                                    Optional<IclockTransaction> iclockTransaction = iclockTransactionService.findById(transactionPresensi.getIclockTransaction());
                                    if (iclockTransaction.isPresent()) {
                                        //epEptransaction.get().setTemperatureF(BigDecimal.valueOf(0));
                                        iclockTransaction.get().setIsSync(true);
                                        iclockTransactionService.save(iclockTransaction.get());
                                        log.info("update data awal to TRUE by sync OUT transactionpresensi id " + transactionPresensi.getIclockTransaction());
                                    }
                                }
                            }
                            i++;
                        }
                    }
                }
            }
        }
    }
}
