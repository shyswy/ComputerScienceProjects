#lang racket
(provide (all-defined-out)) ;; exports the defined variables in this file.

;; definition of structures for MUPL programs - Do NOT change
(struct var  (string) #:transparent)  ;; a variable, e.g., (var "foo")
(struct int  (num)    #:transparent)  ;; a constant number, e.g., (int 17)
(struct add  (e1 e2)  #:transparent)  ;; add two expressions
(struct ifgreater (e1 e2 e3 e4)    #:transparent) ;; if e1 > e2 then e3 else e4
(struct fun  (nameopt formal body) #:transparent) ;; a recursive(?) 1-argument function
(struct call (funexp actual)       #:transparent) ;; function call
(struct mlet (var e body) #:transparent) ;; a local binding (let var = e in body) 
(struct apair (e1 e2)     #:transparent) ;; make a new pair
(struct fst  (e)    #:transparent) ;; get first part of a pair
(struct snd  (e)    #:transparent) ;; get second part of a pair
(struct aunit ()    #:transparent) ;; unit value -- good for ending a list
(struct isaunit (e) #:transparent) ;; evaluate to 1 if e is unit else 0

;; Definitions for extra requirements should be here.
(struct glet (var e body) #:transparent) ;; a global binding that overrides any local binding (similar to the following ML code: let var = e in body)

(struct num-array  (size) #:transparent)  ;; a number array  (initialized to zeroes), e.g., (num-array-var 10)
                                                     ;; e.g. (num-array 4)

(struct num-array-at   (e1 e2) #:transparent) ;; e1 evaluates to num-array and e2 evaluates to racket int (index of the value to access) index starts from 0
                                              ;; (num-array-at (num-array 4) 3)
                                              ;; (num-array-at (num-array 4) 4) ;  this should give a nice error message (like "array access out of bound")
                                              ;; (num-array-at (num-array 4) -1) ;  this should give a nice error message (like "array access out of bound")

(struct num-array-set  (e1 e2 e3) #:transparent) ;; e1 evaluates to a num-array value, e2 evaluates to racket int (index of the value to access), and e3 evaluates to a MUPL int
                                              ;; (num-array-set (num-array 4) 0 (int 42))
                                              ;; (num-array-set (num-array 4) 5 (int 42)) ; this should give a nice error message (like "array access out of bound")
                                              ;; (num-array-set (num-array 4) -1 (int 42)) ; this should give a nice error message (like "array access out of bound")

;; a closure is not in "source" programs; it is what functions evaluate to
(struct closure (env fun) #:transparent) 

;; Problem 1
(define (racketlist->mupllist lst) ;; mupl list >>  (값, (값, 다음 ....)  의 스트림으로 표현 
  (cond [(null? lst) (aunit)] 
        [(pair? lst) (apair (racketlist->mupllist (car lst))(racketlist->mupllist (cdr lst)))]  
        [#t lst]))
(define (mupllist->racketlist lst)
  (cond [(aunit? lst) null]
        [(apair? lst) (cons (mupllist->racketlist (apair-e1 lst)) (mupllist->racketlist (apair-e2 lst)))]
        [#t lst]))

;; Problem 2

;; lookup a variable in an environment
;; Do NOT change this function
(define (envlookup env str)
  (cond [(null? env) (error "unbound variable during evaluation" str)]
        [(equal? (car (car env)) str) (cdr (car env))]
        [#t (envlookup (cdr env) str)]))

;; Do NOT change the two cases given to you.  
;; DO add more cases for other kinds of MUPL expressions.
;; We will test eval-under-env by calling it directly even though
;; "in real life" it would be a helper function of eval-exp.
(define (eval-under-env e env)
  (cond [(var? e) 
         (envlookup env (var-string e))]
        [(int? e)
         e]
        [(add? e) 
         (let ([v1 (eval-under-env (add-e1 e) env)]
               [v2 (eval-under-env (add-e2 e) env)])
           (if (and (int? v1) (int? v2))
               (int (+ (int-num v1) (int-num v2)))
               (error "MUPL addition applied to non-number")))]
        [(ifgreater? e)
         (let ([v1 (eval-under-env (ifgreater-e1 e) env)]
               [v2 (eval-under-env (ifgreater-e2 e) env)])
           (if (and (int? v1) (int? v2))
               (if (> (int-num v1) (int-num v2))
                   (eval-under-env (ifgreater-e3 e) env)
                   (eval-under-env (ifgreater-e4 e) env))
               (error "MUPL comparison applied to non-number")))]
        [(closure? e)
         e]
        [(fun? e)
         (closure env e)]

         ;;함수 를 환경에 저장: (name,closure)  >> closure >> (env, fun)
        ;;최종 형태:  ( (name,closure), 기존환경 ) )
        [(call? e)    
         (let ([clsr (eval-under-env (call-funexp e) env)]
               [arg (eval-under-env (call-actual e) env)])
           (if (closure? clsr)
               (eval-under-env (fun-body (closure-fun clsr)) ;;   eval-under-env e env 중 e   closure 의 저장 형태 (body,env) >> env 는 stream처럼 work 
                               (let ([env (cons   (cons (fun-formal (closure-fun clsr)) arg)   (closure-env clsr))]  
                                            ;;   formal: 함수 정의 환경  env= ( (formal, fun),arg)
                                     [name (fun-nameopt (closure-fun clsr))]) ;;fun 이름 get 
                                 (if name (cons (cons name clsr) env) env)))  ;;( (name, clsr) , env )  해당 이름의 함수 x 시 env 그대로  (env끝) 
               (error "MUPL call applied to non-function")))]

        [(mlet? e) ;;local binding 
         (let* ([v (eval-under-env (mlet-e e) env)]       ;전역값 
                [menv (cons (cons (mlet-var e) v) env)])  ;; 새로운 환경 : ((지역값, 전역값),기존환경)
           (eval-under-env (mlet-body e) menv))] ;;새로운 환경에서 body 실행

       ;;glet >> var,e,body     glbobal binding ( 전역을 덮어쓴다) 
        [(glet? e)            
         (letrec ([var (glet-var e)]
                  [val (eval-under-env (glet-e e) env)];; local 값 
                  [genv (lambda (env) ;;새로운 환경. 재귀 돈다. 
                          (cond [(null? env) (list (cons var val))];;  (전역,지역 ) 
                                [(equal? var (car (car env))) ;; 이미 env의 top에 해당 이름 존재 
                                 (cons (cons var val) (genv (cdr env)))];; 이미 똑같은 값 존재시 기존 값 cdr로 지우고 덮어씀 
                                [(closure? (cdr (car env))) ;;이전 환경이 함수일시 env > genv로 환경 변경 
                                 (let ([clsr (cdr (car env))])
                                           ;;( (name,         clsr) , env ) >>closure = (env,                   fun)      
                                   (cons (cons (car (car env))    (closure (genv (closure-env clsr)) (closure-fun clsr))   );;
                                         (genv (cdr env)))      )]           ;;환경만 genv로 바꿔줌 
                                          ;env 
                                [#t (cons (car env) (genv (cdr env)))]))])
                  (eval-under-env (glet-body e) (genv env)))]
        
        [(apair? e)
         (apair    (eval-under-env (apair-e1 e) env)      (eval-under-env (apair-e2 e) env))]
        [(fst? e)
         (let ([v (eval-under-env (fst-e e) env)])
           (if (apair? v)
               (apair-e1 v)
               (error "MUPL fst applied to non-apair")))]
        [(snd? e)
         (let ([v (eval-under-env (snd-e e) env)])
           (if (apair? v)
               (apair-e2 v)
               (error "MUPL fst applied to non-apair")))]
        [(aunit? e)
         e]
        [(isaunit? e)
         (if (aunit? (eval-under-env (isaunit-e e) env)) (int 1) (int 0))]
        [(num-array? e)
         (if (> (num-array-size e) 0) (make-array-object (num-array-size e)) (aunit))]
        [(num-array-at? e)   ;;(e1,e2) >>   (numarrat 값, 접근할 index( int) )
         (let ([arr (eval-under-env (num-array-at-e1 e) env)] ; mupl-num-array
               [idx (num-array-at-e2 e)]) ; racket-int
           (cond [(not (num-array-object? arr)) (error "MUPL num-array-at e1 applied to non-num-array")]
                 [(not (and (<= 0 idx) (< idx (array-length arr)))) (error "MUPL num-array access out of bound")]
                 [#t (mlist-ref arr idx)]))]
        [(num-array-set? e)    ;;  (e1,e2,e3) >> (numarray값, 접근 index(int) , 바꿀 값 (mupl int)  )
         (let ([arr (eval-under-env (num-array-set-e1 e) env)] ; mupl-num-array
               [idx (num-array-set-e2 e)] ; racket-int
               [v (eval-under-env (num-array-set-e3 e) env)])
           (cond [(not (int? v)) (error "MUPL num-array-set e3 applied to non-int")]
                 [(not (num-array-object? arr)) (error "MUPL num-array-set e1 applied to non-num-array")]
                 [(not (and (<= 0 idx) (< idx (array-length arr)))) (error "MUPL num-array access out of bound")]
                 [#t (begin (set-array-val arr idx v) v)]))]
        [(num-array-to-list? e)
         (let ([arr (eval-under-env (num-array-to-list-e e) env)])
           (cond [(not (num-array-object? arr)) (error "MUPL num-array-to-list applied to non-num-array")]
                 [#t (racketmlist->mupllist arr)]))]
        [#t (error (format "bad MUPL expression: ~v" e))]))

;; Do NOT change
(define (eval-exp e)
  (eval-under-env e null))
        
;; Problem 3

(define (ifaunit e1 e2 e3) (ifgreater (isaunit e1) (int 0) e2 e3))

(define (mlet* lstlst e2)
  (if (null? lstlst)
      e2
      (mlet (car (car lstlst)) (cdr (car lstlst)) (mlet* (cdr lstlst) e2))))   ;;mlet 전역값, 지역값  (전역값,지역값) 제외환경에서 e2 eval 

(define (ifeq e1 e2 e3 e4) ;;if e1>e2 then e3 else e4
  (mlet* (list (cons "_x" e1)(cons "_y" e2))
         (ifgreater (var "_x") (var "_y") e4
                    (ifgreater (var "_y") (var "_x") e4 e3))))

;; Problem 4
(define mupl-map
  (fun "mupl-map" "fn"
       (fun "mupl-map-gen" "lst"
            (ifaunit (var "lst")
                     (aunit)
                     (apair (call (var "fn") (fst (var "lst"))) ;; fn( list ) 첫쨰 
                            (call (var "mupl-map-gen") (snd (var "lst")))))))) ;;남은놈 
  
(define mupl-mapAddN
  (fun "mupl-mapAddN" "I"
       (call mupl-map (fun #f "x" (add (var "x")(var "I"))))))

;; Extra 2
(define (num-array-object? v) ;; hackish implementation for num-array object testing. We assume that if a value is mpair, it is a num-array object.
  (mpair? v))

(define (array-length array)
  (if (eq? (mcdr array) null)
      1
      (+ 1 (array-length (mcdr array)))))

(define (make-array-object length)  
    (if (= length 0)
        null
        (mcons (int 0) (make-array-object (- length 1)))))

(define (set-array-val array index val)
  (if (= index 0)
      (set-mcar! array val)
      (set-array-val (mcdr array) (- index 1) val)))

(define (mlist-ref mlst pos)
  (cond [(null? mlst) (error "mlist-ref: index too large for mlist")]
        [(= pos 0) (mcar mlst)]
        [#t (mlist-ref (mcdr mlst) (- pos 1))]))

; Toy codes
(struct num-array-to-list (e) #:transparent)

(define (mlist->list mlst)
  (cond [(null? mlst) null]
        [#t (cons (mcar mlst) (mlist->list (mcdr mlst)))]))

(define (racketmlist->mupllist mlst)
  (racketlist->mupllist (mlist->list mlst)))