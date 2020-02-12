update sigepen_owner.passagem p set p.data_entrada = add_months(p.data_entrada, -12)
where p.id in (1180, 1966, 1535, 1786, 1297);

update sigepen_owner.passagem p set p.data_entrada = add_months(p.data_entrada, -1)
where p.id in (2326);
