-- Usuario administrador padrao (login: admin / senha: admin123). Troque a senha apos o primeiro acesso.
insert into usuarios (login, senha, perfil, ativo)
select 'admin', '$2a$10$PYS9D8H/aCdZRlQZlDQi7eX4xY00B/tq8MUZysjaNdtTMukOhtWg2', 'ADMIN', 1
from dual
where not exists (select 1 from usuarios where login = 'admin');

update usuarios set perfil = 'ADMIN' where login = 'admin';
