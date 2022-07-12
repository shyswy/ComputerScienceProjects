



datatype pattern = Wildcard | Variable of string | UnitP
| ConstP of int | TupleP of pattern list
| ConstructorP of string * pattern

datatype valu = Const of int | Unit | Tuple of valu list
| Constructor of string * valu



fun distinct l = 
	case l of
		[] => true
		|x::xs => (not(List.exists(fn a => a = x) xs)) andalso distinct xs




fun  trans_list l = 
	case l of
		Variable s => [s]
		|ConstructorP(s,p) => trans_list p
		|TupleP p => foldl(fn (a, acc) => acc @ trans_list a) [] p
		|_ => []	



fun check_pat p=
        let val lst=trans_list p
        in
          distinct lst
        end











  
fun all_chk f xs = let
    fun chk (SOME x) (SOME acc) = SOME (acc @ x)
      | chk _ _ = NONE
  in
    foldl (fn (x,tmp) => chk (f x) tmp) (SOME []) xs
  end



fun match(v, p) = 
	case p of
		Wildcard => SOME []
		|Variable s => SOME [(s, v)]
		|UnitP =>(case v of
			        Unit => SOME []
				|_ => NONE)
		|ConstP pp =>
			(case v of
				Const vv => if pp = vv then SOME [] else NONE
				|_ => NONE)



	        | TupleP pp =>( case v of 
                              Tuple vv => if length(pp)=length(vv) then all_chk match (ListPair.zip (vv,pp))
                                           else NONE
                              | _ => NONE



		|ConstructorP(s1, pt) =>
			(case v of
				Constructor(s2, vl) => if s1 = s2 then match(vl, pt) else NONE
				|_ => NONE)












type name = string

datatype RSP = ROCK
		|SCISSORS
		|PAPER

datatype 'a strategy = Cons of 'a * (unit -> 'a strategy)

datatype tournament = PLAYER of name * (RSP strategy ref)     
			| MATCH of tournament * tournament





fun onlyOne(one:RSP) = Cons(one, fn() => onlyOne(one))

fun alterTwo(one:RSP, two:RSP) = Cons(one, fn() => alterTwo(two, one))

fun alterThree(one:RSP, two:RSP, three:RSP) = Cons(one, fn() => alterThree(two, three, one))

val r = onlyOne(ROCK); 
val s = onlyOne(SCISSORS); 
val p = onlyOne(PAPER); 
val rp = alterTwo(ROCK, PAPER); 
val sr = alterTwo(SCISSORS, ROCK);
val ps = alterTwo(PAPER, SCISSORS); 
val srp = alterThree(SCISSORS, ROCK, PAPER);

fun next(strategyRef) = 
	let 
		val Cons(rsp, func) = !strategyRef 
	in
		strategyRef := func();
		rsp
	end

fun game_result(PLAYER(p1, r_p1), PLAYER(p2, r_p2)) = 
	let
		val v = (next r_p1, next r_p2)
	in
		(case v of
			(ROCK, SCISSORS) => PLAYER(p1, r_p1)
			|(ROCK, PAPER) => PLAYER(p2, r_p2)
			|(SCISSORS, ROCK) => PLAYER(p2, r_p2)
			|(SCISSORS, PAPER) => PLAYER(p1, r_p1)
			|(PAPER, ROCK) => PLAYER(p1, r_p1)
			|(PAPER, SCISSORS) => PLAYER(p2, r_p2)
			|_ => game_result(PLAYER (p1, r_p1), PLAYER (p2, r_p2) )
                 )
	end

fun whosWinner(t) =
	case t of
		PLAYER (n, st) => PLAYER(n, st)
		|MATCH (a, b) => game_result(whosWinner(a), whosWinner(b))



val winner = whosWinner(MATCH(PLAYER("s", ref s), MATCH(PLAYER("rp", ref rp), PLAYER("r", ref r))));
