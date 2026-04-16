#!/bin/bash
su - omm -c "gsql -d postgres -p 5432 -c 'SELECT version();'"
