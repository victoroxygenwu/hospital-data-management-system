#!/bin/bash
su - omm -c "gsql -d ruoyi_vue_pro -p 5432 -c \"SELECT tablename FROM pg_tables WHERE schemaname='public' ORDER BY tablename;\""
