#!/bin/bash
su - omm -c "cat /var/lib/opengauss/data/pg_hba.conf | grep -v '^#' | grep -v '^$'"
