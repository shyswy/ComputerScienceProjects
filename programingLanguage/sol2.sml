datatype expr = NUM of int
| PLUS of expr * expr
| MINUS of expr * expr

datatype formula = TRUE
| FALSE
| NOT of formula
| ANDALSO of formula * formula
| ORELSE of formula * formula
| IMPLY of formula * formula
| LESS of expr * expr


fun ex e=
case e of 
PLUS(e3,e4) => ex(e3)+ex(e4)
| MINUS(e3,e4) => ex(e3)-ex(e4)
|NUM i => i




fun eval e=
case e of
TRUE => true
| FALSE => false
| NOT e1 => not (eval(e1))
| ANDALSO (e1,e2) => (eval(e1)) andalso (eval(e2))
| ORELSE (e1,e2) => (eval(e1)) orelse (eval(e2))
| IMPLY (e1,e2) => (not (eval(e1))) andalso (eval(e2))
| LESS(e1,e2) => if  ( ex(e1) ) <(  ex(e2) ) then true else false

type name = string
datatype metro = STATION of name
| AREA of name * metro
| CONNECT of metro * metro


fun mk_list (e1,e2)=
case e2 of
[]=>[]
|x::xs' => if e1=x then mk_list(e1,xs') else x::mk_list(e1,xs')


fun connect (e1,e2)=
case e1 of
[]=>e2
|x::xs'=>x::connect(xs',e2)

(*metro>string list*)
fun area e=
case e of
AREA(e1,e2) => mk_list(e1,area(e2))
|CONNECT(e1,e2)=> connect(area(e1),area(e2)) 
|STATION e1 => e1::[] 




fun checkMetro e =
case e of
AREA(e1,e2) => if area(e)=[] then true else false
| CONNECT(e1,e2)=> false
| STATION e1 => false

datatype 'a lazyList = nullList 
| cons of 'a * (unit-> 'a lazyList)
(*
fun  get_List( cons( e,f):'a lazyList ,n )= e::get(f(),n-1)
| get (f,0) =[]
*)
 


fun ser (e) = cons(e,fn()=>ser(e+1))

fun seq ( e1, e2):int lazyList =
if e1<e2+1 then cons(e1,fn()=>seq(e1+1,e2))
	else nullList


fun infSeq(e):int lazyList=cons(e,fn()=>infSeq(e+1))




fun filterMultiples(cons(e:int,f:unit->int lazyList),n):int lazyList=	
if (e mod n=0) then filterMultiples(f(),n)
	else cons(e, fn()=>filterMultiples(f(),n) )

| filterMultiples(nullList,_)=nullList

(*fun firstN ( f,n):'a list =get_List(f,n) *)
fun firstN( cons(e,f) ,n )=   
 if ( (n=0))  then  []
                else e:: firstN(f(),n-1)
| firstN(nullList,_) =[]  
				   


fun Nth(cons(e,f),n):'a option=
if(n=1) then SOME(e) 
	else Nth(f(),n-1)
|Nth(nullList,_)=NONE


fun sieve(cons(e:int,f:unit->int lazyList) ):int lazyList=
cons(e,fn()=>sieve(filterMultiples(f(),e) ) )
|sieve(nullList):int lazyList = nullList

fun primes (unit)= sieve(cons(2, fn()=>filterMultiples(infSeq(2),2) ) )  

 
