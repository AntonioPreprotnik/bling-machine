#!/bin/sh

export CHAMBER_KMS_KEY_ALIAS=aws/ssm

echo "Starting application"
exec /usr/bin/chamber exec "og-backend" -- \
  java -verbose -jar app.jar
