alter table usuarios add column perfil varchar(20) not null default 'USER';
alter table usuarios add column ativo tinyint default 1;
alter table usuarios add constraint uk_usuarios_login unique(login);
