CREATE RECURSIVE VIEW R ( DD , a , b , pp , value ) AS 
(
SELECT 1 , a , b , 1 , value   FROM  graph
UNION ALL
SELECT DD + 1 ,  R.a , R.b , R.pp * graph.pp , R.s + graph.s
FROM R JOIN graph ON R . b = graph . a 
WHERE DD < 10
)
;
SELECT  R.a , b ,  N1.name, N1.ZIP, N2.name, N2.ZIP  INTO QIJ FROM R
  JOIN N AS N1  on R.a = N1.a 
  JOIN N AS N2  on R.a = N2.a ;
