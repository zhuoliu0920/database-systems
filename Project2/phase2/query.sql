CREATE RECURSIVE VIEW R ( DD , a , b , pp , value ) AS 
(
SELECT 1 , a , b , 1 , value   FROM  graph
UNION ALL
SELECT DD + 1 ,  R . a , R . b , R . pp * graph . pp , R . s + graph . s
FROM R JOIN graph ON R . b = graph . a 
WHERE DD < 10
)
;
SELECT DISTINCT a , b  FROM R;
