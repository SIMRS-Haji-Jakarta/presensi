#!/bin/bash
# created by bang.pardi

cd /opt/aplikasi-presensi
git pull
mvn clean package
systemctl restart aplikasi-presensi
systemctl status aplikasi-presensi