CREATE RECURSIVE VIEW R ( DD , a , b , pp , value ) AS 
(
SELECT 1 , a , b , 1 , value   FROM  graph
UNION ALL
SELECT DD + 1 ,  R.a , R.b , R.pp * graph.pp , R.s + graph.s
FROM R JOIN graph ON R . b = graph . a 
WHERE DD < 10
)
;
SELECT  R.a , b ,  N1.name, N2.name as name2, DD INTO QJ FROM R
  JOIN N as  N1 on R.a = N1.a JOIN N as N2 on R.b=N2.a;
