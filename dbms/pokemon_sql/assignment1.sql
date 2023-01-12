# create table
CREATE TABLE City (
  name varchar(32),
  description varchar(32),
  primary key (name)
 );
 
 CREATE TABLE Trainer (
   id INT(11) unsigned AUTO_INCREMENT,
   name varchar(32),
   hometown varchar(32),
   primary key (id),
   foreign key (hometown) references City (name)
 );
 
 CREATE TABLE Gym (
   leader_id INT(11) unsigned,
   city varchar(32),
   foreign key (leader_id) references Trainer (id),
   foreign key (city) references City (name)
 );
 
 CREATE TABLE Pokemon (
   id INT(11) unsigned,
   name varchar(32),
   type varchar(32),
   primary key (id)
 );
 
 CREATE TABLE CatchedPokemon (
   id INT(11) unsigned auto_increment,
   owner_id INT(11) unsigned,
   pid INT(11) unsigned,
   level INT(11) unsigned,
   nickname varchar(32),
   primary key (id),
   foreign key (owner_id) references Trainer (id),
   foreign key (pid) references Pokemon (id)
 );
 
 CREATE TABLE Evolution (
   before_id INT(11) unsigned,
   after_id INT(11) unsigned,
   foreign key (before_id) references Pokemon (id),
   foreign key (after_id) references Pokemon (id)
 );
 
# insert.sql

INSERT INTO City (name, description) VALUES
('Blue City', 'Waterpark'),
('Sangnok City', 'Amazon'),
('Brown City', 'Famous Harbor'),
('Rainbow City', 'Huge City with Casino');

INSERT INTO Trainer (name, hometown) VALUES
('Red', 'Sangnok City'),
('Green', 'Sangnok City'),
('Blue', 'Sangnok City'),
('Ash', 'Sangnok City'),
('Yellow', 'Sangnok City'),
('Gold', 'Blue City'),
('Matis', 'Brown City'),
('Erika', 'Rainbow City'),
('Misty', 'Blue City');

INSERT INTO Gym (leader_id, city) VALUES
((SELECT id from Trainer where name='Misty'), 'Blue City'),
((SELECT id from Trainer where name='Green'), 'Sangnok City'),
((SELECT id from Trainer where name='Matis'), 'Brown City'),
((SELECT id from Trainer where name='Erika'), 'Rainbow City');

INSERT INTO Pokemon (id, name, type) VALUES
(1, 'Bulbasaur', 'Grass'),
(2, 'Ivysaur', 'Grass'),
(3, 'Venusaur', 'Grass'),
(4, 'Charmander', 'Fire'),
(5, 'Charmeleon', 'Fire'),
(6, 'Charizard', 'Fire'),
(7, 'Squirtle', 'Water'),
(8, 'Wartortle', 'Water'),
(9, 'Blastoise', 'Water'),
(17, 'Pidgeotto', 'Normal'),
(18, 'Pidgeot', 'Normal'),
(25, 'Pikachu', 'Electric'),
(26, 'Raichu', 'Electric'),
(37, 'Vulpix', 'Fire'),
(38, 'Ninetales', 'Fire'),
(46, 'Paras', 'Grass'),
(47, 'Parasect', 'Grass'),
(54, 'Psyduck', 'Water'),
(55, 'Golduck', 'Water'),
(84, 'Doduo', 'Normal'),
(85, 'Dodrio', 'Normal'),
(116, 'Horsea', 'Water'),
(117, 'Seadra', 'Water'),
(126, 'Magmar', 'Fire'),
(129, 'Magikarp', 'Water'),
(130, 'Gyarados', 'Water'),
(131, 'Lapras', 'Water'),
(133, 'Eevee', 'Normal'),
(137, 'Porygon', 'Normal'),
(150, 'Mewtwo', 'Psychic'),
(151, 'Mew', 'Psychic'),
(152, 'Chikorita', 'Grass'),
(172, 'Pichu', 'Electric'),
(249, 'Lugia', 'Psychic'),
(251, 'Celebi', 'Psychic');

INSERT INTO Evolution (before_id, after_id) VALUES
(1,2),
(2,3),
(4,5),
(5,6),
(7,8),
(8,9),
(17,18),
(25,26),
(37,38),
(46,47),
(54,55),
(84,85),
(116,117),
(129,130),
(172,25);

INSERT INTO CatchedPokemon (pid, owner_id, level, nickname) VALUES
(6, (SELECT id from Trainer where name='Green'), 70, 'After eating hot peppers'),
(3, (SELECT id from Trainer where name='Red'), 80, 'Funny Smell'),
(25, (SELECT id from Trainer where name='Red'), 10, 'AAA'),
(25, (SELECT id from Trainer where name='Ash'), 50, 'Mouse'),
(26, (SELECT id from Trainer where name='Red'), 60, 'AA'),
(131, (SELECT id from Trainer where name='Misty'), 80, 'Laputa'),
(131, (SELECT id from Trainer where name='Ash'), 50, 'Turtle'),
(133, (SELECT id from Trainer where name='Green'), 5, 'Fluffy'),
(150, (SELECT id from Trainer where name='Red'), 100, 'Spoon killer'),
(152, (SELECT id from Trainer where name='Gold'), 1, 'Salad'),
(152, (SELECT id from Trainer where name='Gold'), 2, 'Piece of cake'),
(8, (SELECT id from Trainer where name='Blue'), 20, 'Ninja Turtle'),
(117, (SELECT id from Trainer where name='Blue'), 20, 'Water gun'),
(38, (SELECT id from Trainer where name='Green'), 50, 'Ahri'),
(55, (SELECT id from Trainer where name='Green'), 65, 'Donald Duck'),
(26, (SELECT id from Trainer where name='Yellow'), 60, 'First friend'),
(84, (SELECT id from Trainer where name='Yellow'), 3, 'Cho\'gall'),
(85, (SELECT id from Trainer where name='Yellow'), 3, 'Three-headed dumb'),
(46, (SELECT id from Trainer where name='Erika'), 30, 'Mushroom'),
(152, (SELECT id from Trainer where name='Erika'), 5, 'Chikorita'),
(130, (SELECT id from Trainer where name='Misty'), 90, 'Worm'),
(25, (SELECT id from Trainer where name='Matis'), 40, 'Free Battery'),
(26, (SELECT id from Trainer where name='Matis'), 50, 'Huge Battery'),
(172, (SELECT id from Trainer where name='Matis'), 10, 'Small Battery'),
(130, (SELECT id from Trainer where name='Red'), 95, 'Big yawn');

#gruop by 는 PK 로 하자! (동명이인 나오면 문제 발생..!)

#1
select name
from Pokemon
where type='Grass'
order by name;

#2
select name
from Trainer
where hometown in ('Brown City','Rainbow City');

#3
select distinct type
from Pokemon
order by type
;

#4  null at city...
select name
from City
where name like "B%" 
order by name
;

#5
select hometown
from Trainer
where name not like "M%"
order by hometown;

#6
select distinct nickname
from CatchedPokemon
where level=( select MAX( level) 
	from CatchedPokemon
	)
order by nickname;

#7
select name
from Pokemon
where name like "A%" or name like"E%" or name like"i%" or name like "O%" or name like"U%"
order by name;

#8
select avg(level) 
from CatchedPokemon;


#9
select max(level)
from CatchedPokemon
where owner_id= (select id 
from Trainer
where name= "Yellow"
);

#10
select distinct hometown
from Trainer
order by hometown;

#11
#join~~~~ on x   >  x 의 조건으로 join. 
#naturaljoin>> 중복제거. 공통 col로 조인 따라서 id로 join되어서 의도와달라짐

select Trainer.name, CatchedPokemon.nickname
from Trainer
join CatchedPokemon on Trainer.id=CatchedPokemon.owner_id

where CatchedPokemon.nickname like "A%"
order by Trainer.name
;



#12  
#거꾸로 ( 끝> 처음 으로 훑고 가면서 하자 어차피 select에 결과 쥐어줘야함

select name
from Trainer join Gym on Trainer.id=Gym.leader_id
where Gym.city= (select name from City where description="Amazon")
;



#13
/*각 트레이너가 가진 전기 포켓몬 수
(select owner_id ,count(*) as cnt
from (select * from Pokemon where type="Electric") as e_p
join CatchedPokemon on CatchedPokemon.pid=e_p.id
group by owner_id ) ;
*/

select owner_id,cnt
from (select owner_id ,count(*) as cnt # 각 트레이너가 가진 fire 타입 포켓몬 수를 오름차순, 제일 앞에꺼= max
from (select * from Pokemon where type="Fire") as e_p
join CatchedPokemon on CatchedPokemon.pid=e_p.id
group by owner_id )  a
order by cnt desc
limit 1
;


#14
select distinct type
from Pokemon
where id like "_"
order by id desc;

#15
select count(*)
from Pokemon
where type != "Fire";

#16

select Pokemon.name 
from Pokemon 
join
(select before_id 
from Evolution
where before_id>after_id )a on before_id=Pokemon.id
order by name
;

#17
select avg(level)
from CatchedPokemon join Pokemon on catchedpokemon.pid=Pokemon.id
where Pokemon.type="Water"
;

#18

select nickname
from catchedpokemon
where level=
(select max(level)
from Gym join CatchedPokemon on CatchedPokemon.owner_id=Gym.leader_id);

#19
#평균이 가장 높은 트레이너
select name  #유저별 평균 레벨
from Trainer join CatchedPokemon on CatchedPokemon.owner_id=Trainer.id and Trainer.hometown="Blue City"  
group by owner_id having avg(level)=(select max(avg_lev)
from (select owner_id, avg(level) AS avg_lev
      from Trainer join CatchedPokemon on CatchedPokemon.owner_id=Trainer.id and Trainer.hometown="Blue City"  
      group by owner_id) As max_avg)
order by name;


/*
select max(avg_lev)     평균의 최대값.
from (select owner_id, avg(level) AS avg_lev
      from Trainer join CatchedPokemon on CatchedPokemon.owner_id=Trainer.id and Trainer.hometown="Blue City"  
      group by owner_id) As max_avg;
      
      */
      
      
#20

select Pokemon.name
from Trainer join CatchedPokemon join Pokemon   on Trainer.id=CatchedPokemon.owner_id and CatchedPokemon.pid=Pokemon.id
and Pokemon.id in (select before_id from Evolution )  #Trainer, pokemon, catchedpokemon 테이블 알맞게 합친 뒤, 진화가능한거만 뽑아놓기
where hometown in
(select hometown from Trainer group by hometown having count(*)=1) and Pokemon.type="Electric"; #같은 출신 x 트레이너, 전기 속성만 선택

/*  같은 출신 없는 트레이너 (중복 값의 개수가 1 인 row )
select name
from Trainer
where hometown in
(select hometown from Trainer group by hometown having count(*)=1);
*/


select  sum(level) as sum_lev ,owner_id
from Trainer join CatchedPokemon on Trainer.id in (select leader_id from Gym)
group by owner_id
;



#21
select  sum(level) as sum_lev ,Trainer.name
from CatchedPokemon join Trainer on Trainer.id=catchedpokemon.owner_id
group by owner_id having owner_id in (select leader_id from Gym)
order by sum_lev desc
;




#22
select hometown 
from Trainer
group by hometown having count(*) = (select max(b.cnt) from (select count(*) as cnt from Trainer group by hometown)b ) ;



#23
# 상록 시티에 잡힌 포켓몬 테이블 에도 있고, 브라운 시티에 잡힌 포켓몬 테이블 에도 있는 포켓몬
select distinct Pokemon.name
from Pokemon 
where Pokemon.id in 
(select catchedpokemon.pid 
from CatchedPokemon join Trainer on catchedpokemon.owner_id=Trainer.id
where Trainer.hometown = "Sangnok City" ) and
Pokemon.id in 
(select catchedpokemon.pid 
from CatchedPokemon join Trainer on catchedpokemon.owner_id=Trainer.id
where Trainer.hometown = "Brown City" )
order by name
;

#24

select trainer.name
from Trainer join catchedpokemon join pokemon on catchedpokemon.owner_id=trainer.id and pokemon.id=catchedpokemon.pid
where pokemon.name like "P%" and hometown="Sangnok City";


#25
#????
select t_name ,p_name
from
(select pokemon.name as p_name ,catchedpokemon.owner_id,trainer.name as t_name
from  catchedpokemon join pokemon join Trainer on pokemon.id=catchedpokemon.pid and catchedpokemon.owner_id=trainer.id
order by t_name
 )a
order by p_name ;

#26
select name
from pokemon
where pokemon.id in
(select before_id
 from (select before_id,after_id from evolution where before_id not in (select after_id from evolution   ) )a  # 1단계진화체 ( 이전 버전x)
 where a.after_id not in (select before_id from evolution)  ) #다음진화가 없는 진화체( 이미 1회 이상진화된 놈)
 order by name;
 
 

 #27
 select b.nickname
 from (select leader_id,trainer.name from gym join trainer where leader_id=trainer.id and gym.city="Sangnok City")a# 상록시티 관장의 id, 이름
 join 
 (select nickname,owner_id from catchedpokemon join pokemon on catchedpokemon.pid=pokemon.id
 where type="Water" )b  # 물타입 포켓몬의 닉네임과 주인 id
 on a.leader_id = b.owner_id
 order by b.nickname;
 
 
 
 #28
 
 #select trainer.name
 #from trainer 
 
select trainer.name 

from  (select owner_id, count(*) as cnt
 from (select catchedpokemon.pid,owner_id from catchedpokemon where pid  in (select after_id from evolution) )a
 group by owner_id ) b#각 트레이너의 진화된 포켓몬 수   
 join trainer on trainer.id=owner_id  
 
 where cnt>2
 
 order by trainer.name
 
 ;
 
 #29
 select pokemon.name
 from pokemon 
 where id not in (select pid from catchedpokemon)
 order by name;
 
 #30
 select max(level) as max_lev
 from trainer join catchedpokemon on catchedpokemon.owner_id = trainer.id
 group by hometown
 order by max_lev desc;
 
 
 
 #31
 /*
 select name
from pokemon
where pokemon.id in
(select after_id as e3
 from evolution 
 where after_id in 
 (select before_id as e1 ,after_id as e2 from evolution where before_id not in (select after_id from evolution   ) )   # 1단계진화체 ( 이전 버전x)
 );
 #2째 진화체의 다음진화체 (3째)
 */
 
select  *  #evolution.after_id as e3
from
 (select before_id as e1 ,after_id as e2 from evolution where before_id not in  
 (select after_id from evolution  ) )a  # 1단 진화 ,2단진화
 join evolution on 
 a.e2 =before_id
 ;
 #(select before_id from evolution)  ; # a.after_id > 2단진화 
 
 select * #a.e1,a.e2,evolution.after_id as e3  #evolution.after_id as e3
from
 (select before_id as e1 ,after_id as e2 from evolution join pokemon on before_id=pokemon.id or before_id=pokemon.id  )a  # 1단 진화 ,2단진화
 join evolution 
 on 
 a.e2 =before_id
 ;
 
 select e1_id,e1_name,e2_name,e3_name
 from
( select e1_id,e1_name,e2_id,e2_name
 from
 ( select * from evolution join (select id as e1_id,name as e1_name from pokemon) b on before_id=b.e1_id  ) e1_list 
 # e1_list 는 e1 이름, id 를 매칭해서 붙인다
 join
 (select id as e2_id,name as e2_name  from pokemon) d on (e1_list.after_id=d.e2_id )   ) e2_list   # e는 e2의 id, 이름
 #e2_list 는 e2의 이름, id 를 매칭해서 붙이고  e1_list와 붙여준다. 
 join
 (select before_id,pokemon.name as e3_name,after_id as e3_id from evolution join pokemon on after_id=pokemon.id ) e3_list   # f는 e3의 id, 이름
 on e2_list.e2_id= e3_list.before_id
 #e3_list 는 e3의 이름, id 를 매칭해서 붙이고  e2_list와 붙여준다. 
 
 order by e1_id
 ;
 
 
 
 
 






























