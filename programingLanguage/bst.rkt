#lang racket
(provide (all-defined-out))

(define t1 '(8 (3 (1 () ()) (6 (5 () ()) (7 () ()))) (3 () (14 (13 ()()) ()))))
(define a '(8(3(1 () () ) (6 (5 () () ) (7 () () ))) (10 () (14 ( 12 () () ) () ))) )
;1. check_bst
(define (check_bst lst)
  (if (null? lst) #t
  (letrec
     (
      [lt ( car (cdr lst) )]
      [rt  (car(cdr (cdr lst))) ]
      
      [now (car lst)]
      [left ( lambda(left_tree) 
            (if(null? left_tree) #t
               (and (< (car left_tree)   now) (check_bst left_tree))
            ))]
       [right ( lambda(right_tree) 
            (if(null? right_tree) #t
               (and (> (car right_tree) now) (check_bst right_tree))
            ))]
     )
     (and (left lt) (right rt) ))))      
;2. apply
(define (apply f lst)
  (if (null? lst) null
      (list     (f (car lst))       (apply f (car(cdr lst)))     (apply f (car(cdr(cdr lst)))))))     ;  (list now left right) 각 함수에서 now에 f 적용      

;3. equals
(define (isleaf lst)
  (if (and (null?(cadr lst)) (null? (caddr lst) ) ) #t  ;; left / right empty?? 
      #f) )
(define (find  nodeVal tree)     ; if nodeval 이 tree 에 존재?? 
  (if (null? tree)#f
      (or (= nodeVal (car tree)) (find nodeVal (cadr tree)) (find nodeVal (caddr tree))))) ;; 현재값과 비교, 왼쪽전체 비교, 오른쪽 전체 비교 
(define (my_if x y z) ; thunk
  (if x (y) (z) ) )
(define (same lst1 lst2)
  (if (null? lst1)#t
  (my_if (find (car lst1) lst2 ) (lambda() (and (same (cadr lst1) lst2) (same (caddr lst1) lst2) ) ) ;;tree1의 현재 값이 tree2에 존재시 left, right child로
     (lambda() #f))
  )
)








  (define (equals t1 t2)
  (and (same t1 t2)(same t2 t1)))









            





            