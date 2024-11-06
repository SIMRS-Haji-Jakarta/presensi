create schema employ;
create schema presensi;
create schema referensi;

create table referensi.jenis_pegawai (id serial not null, nama varchar(255), kategori_pegawai int8, primary key (id));
create table referensi.kategori_pegawai (id serial not null, nama varchar(255), primary key (id));
create table referensi.status_keaktifan (id serial not null, nama varchar(255), primary key (id));
create table referensi.unit (id serial not null, nama varchar(255), primary key (id));

create table employ.golongan (id serial not null, nama varchar(255), pangkat varchar(255), primary key (id));
create table employ.jabatan_fungsional (id serial not null, nama varchar(255), primary key (id));

create table presensi.absensi (id varchar(36) not null, created_at timestamp, created_by varchar(30), updated_at timestamp, updated_by varchar(30), tanggal DATE, tanggal_out DATE, waktu_in TIME, waktu_in_ip varchar(64), waktu_in_lat float8, waktu_in_lgn float8, waktu_out TIME, waktu_out_ip varchar(64), waktu_out_lat float8, waktu_out_lgn float8, id_pegawai int8, id_status_presensi varchar(36), primary key (id));
create table presensi.admin_unit (id varchar(36) not null, keterangan varchar(250), nama varchar(50), pj_jabatan varchar(30), id_pegawai int8, pj_pegawai int8, primary key (id));
create table presensi.admin_unit_kerja (id_admin_unit varchar(36) not null, unitKerja_id int8 not null, primary key (id_admin_unit, unitKerja_id));
create table presensi.area (id varchar(36) not null, koordinate TEXT, nama varchar(255), primary key (id));
create table presensi.face_pegawai (id varchar(36) not null, created_at timestamp, created_by varchar(30), updated_at timestamp, updated_by varchar(30), bio_index int4, bio_no int4, bio_type int4, biodata TEXT, duress int4, major_ver varchar(30), minor_ver varchar(30), valid int4, id_pegawai int8, primary key (id));
create table presensi.foto_profile (id varchar(36) not null, created_at timestamp, created_by varchar(30), updated_at timestamp, updated_by varchar(30), status boolean, url_foto varchar(250), id_pegawai int8, primary key (id));
create table presensi.hari_kerja (tanggal DATE not null, id_jam_kerja varchar(36), id_status_hari varchar(36), primary key (tanggal));
create table presensi.jadwal_kerja_shift (id varchar(36) not null, created_at timestamp, created_by varchar(30), updated_at timestamp, updated_by varchar(30), tanggal DATE, id_admin_unit varchar(36), id_jam_kerja_shift varchar(36), id_pegawai int8, primary key (id));
create table presensi.jam_kerja (id varchar(36) not null, datang TIME not null, nama varchar(50), pulang TIME not null, primary key (id));
create table presensi.jam_kerja_shift (id varchar(36) not null, datang TIME not null, kode varchar(3), nama varchar(50), pulang TIME not null, primary key (id));
create table presensi.log_presensi (id serial not null, created_at timestamp, created_by varchar(30), updated_at timestamp, updated_by varchar(30), is_sync boolean, no_absen varchar(20), op varchar(20), sn varchar(50), stamp varchar(20), status varchar(5), verify int4, waktu timestamp, primary key (id));
create table presensi.mesin_presensi (id serial not null, ip_address varchar(50), kode_area int4, lokasi varchar(200), nama varchar(255), sn varchar(50), primary key (id));
create table presensi.mesin_presensi_pegawai (id serial not null, created_at timestamp, created_by varchar(30), updated_at timestamp, updated_by varchar(30), id_mesin_presensi int8, id_pegawai int8, primary key (id));
create table presensi.mesin_presensi_unit (id serial not null, created_at timestamp, created_by varchar(30), updated_at timestamp, updated_by varchar(30), id_admin_unit varchar(36), id_mesin_presensi int8, primary key (id));
create table presensi.pegawai_presensi_online (id varchar(36) not null, created_at timestamp, created_by varchar(30), updated_at timestamp, updated_by varchar(30), oleh varchar(255), tanggal timestamp, id_pegawai int8, primary key (id));
create table presensi.pegawai_shift (id varchar(36) not null, created_at timestamp, created_by varchar(30), updated_at timestamp, updated_by varchar(30), id_admin_unit varchar(36), id_pegawai int8, primary key (id));
create table presensi.pengaduan (id varchar(36) not null, created_at timestamp, created_by varchar(30), updated_at timestamp, updated_by varchar(30), catatan varchar(250), deskripsi varchar(250), tanggal timestamp, tanggal_mulai DATE, tanggal_selesai DATE, url_bukti varchar(250), id_pegawai int8, id_status_verifikasi varchar(36), primary key (id));
create table presensi.pengajuan (id varchar(36) not null, created_at timestamp, created_by varchar(30), updated_at timestamp, updated_by varchar(30), catatan varchar(250), deskripsi varchar(250), is_url boolean, status_proses boolean, tanggal timestamp, tanggal_mulai DATE, tanggal_selesai DATE, url_bukti varchar(250), url_share varchar(250), id_pegawai int8, id_status_presensi varchar(36), id_status_verifikasi varchar(36), primary key (id));
create table presensi.status_hari (id varchar(36) not null, kode varchar(5), nama varchar(20), primary key (id));
create table presensi.status_presensi (id varchar(36) not null, kode varchar(5), nama varchar(20), primary key (id));
create table presensi.status_verifikasi (id varchar(36) not null, kode varchar(5), nama varchar(20), primary key (id));
create table presensi.unit_kerja_presensi (id serial not null, nama varchar(255), parent int8, unit int8, primary key (id));
create table presensi.file_profile (id varchar(36) not null, data bytea, nama varchar(250), type varchar(100), id_foto_profile varchar(36), primary key (id));
create table presensi.file_pengajuan (id varchar(36) not null, data bytea, nama varchar(250), type varchar(100), id_pengajuan varchar(36), primary key (id));

create table public.konfigurasi (id serial not null, keterangan varchar(255), nama varchar(255), nilai varchar(255), primary key (id));
create table public.pegawai (id serial not null, gelarbelakang varchar(255), gelardepan varchar(255), is_face boolean, kelamin varchar(20), nama varchar(255), ktp varchar(50), mycode varchar(100), no_absen varchar(10), spesialisasi1 varchar(255), tanggallahir DATE, uuid varchar(36), golongan_pegawai int8, jabatan_fungsional int8, jenis_pegawai int8, status_kepegawaian int8, unit_kerja_presensi int8, primary key (id));
create table public.tbmrole (roleid varchar(50) not null, rolename varchar(255), primary key (roleid));
create table public.tbmuser (userid varchar(255) not null, aktif boolean, userpassword varchar(255), pegawai int8, userrole varchar(50), primary key (userid));

alter table if exists public.pegawai add constraint FK64vje6w124jch80ljugvjnjqv foreign key (golongan_pegawai) references employ.golongan;
alter table if exists public.pegawai add constraint FKn33t8rxgdtpbf14r6l75qkhi foreign key (jabatan_fungsional) references employ.jabatan_fungsional;
alter table if exists public.pegawai add constraint FK3sg7rbhy0i6mqr4twtq3rph1k foreign key (jenis_pegawai) references referensi.jenis_pegawai;
alter table if exists public.pegawai add constraint FK3umfndw3h40etg2lpwg32u9qk foreign key (status_kepegawaian) references referensi.status_keaktifan;
alter table if exists public.pegawai add constraint FKhl4d4pr3g0oxko6k3f53cem1h foreign key (unit_kerja_presensi) references presensi.unit_kerja_presensi;