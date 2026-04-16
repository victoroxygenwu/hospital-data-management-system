#!/bin/bash
su - omm -c "gsql -d postgres -p 5432 -c \"
CREATE USER gaussdb WITH PASSWORD 'Hospital@2026';
CREATE DATABASE ruoyi_vue_pro OWNER gaussdb;
GRANT ALL PRIVILEGES ON DATABASE ruoyi_vue_pro TO gaussdb;
\"" 
