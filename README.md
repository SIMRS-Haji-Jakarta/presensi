# Aplikasi Presensi RSHAJI


## Configuration Database
- install postgresql with docker
```
docker run -d --name app-presensi -e POSTGRES_DB=db-presensi-dev -e POSTGRES_USER=presensi-dev -e POSTGRES_PASSWORD=8sSOt73zi5HP -e PGDATA=/var/lib/postgresql/data/pgdata -p 5432:5432 postgres:10.7
docker exec -i app-presensi psql -U presensi-dev postgres -c "create database wdms;"
```
- for test case only
```
docker exec -i app-presensi psql -U presensi-dev db-presensi-dev -c "create schema referensi; create schema employ; create schema presensi;"
```

## Demo User
- Admin
```
username: supardi
password: root123
```

## Set Up Path Storage
- Windows
```
file application.properties

example:
app.file.storage.mapping = ${FILE_STORAGE_PATH:file:///D:/EProject/aplikasi-presensi-rshaji/uploads/}
```

- Linux
```
file application.properties

example:
app.file.storage.mapping = ${FILE_STORAGE_PATH:file:~/Desktop/aplikasi-presensi-rshaji/uploads/}
```

## Intergrasi WDMS
Modifikasi database pada table ep_eptransaction, digunakan untuk mengupdate data apabila data sudah tersimpan ke database presensi
```
alter table ep_eptransaction add column is_sync boolean default false;
alter table personnel_employee add column id_pegawai int8;
```

Buat view untuk baca data terbaru
```
--drop view transaction_presensi;
--create or replace view transaction_presensi as
select 
	t.id as id_eptransaction,
	it.id as iclock_transaction,
	e.emp_code as no_absen,
	it.punch_state as in_out,
	t.check_datetime as date_full,
	t.check_date as date_date,
	t.check_time as date_time,
	ie.real_ip as ip_address, 
	t.is_sync as is_sync,
	e.id_pegawai as pegawai
from ep_eptransaction t
inner join iclock_transaction it on (t.emp_id=it.emp_id and t.check_datetime=it.punch_time)
left join personnel_employee e on (t.emp_id=e.id)
left join iclock_terminal ie on (t.terminal_id=ie.id)
where t.is_sync='f'
order by t.change_time desc;
```

## Services
- check .NET version
```
reg query "HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\NET Framework Setup\NDP\v4\full" /v version
```
- create service
```
WinSW.NET4.exe install
WinSW.NET4.exe start
WinSW.NET4.exe stop
WinSW.NET4.exe uninstall
```
