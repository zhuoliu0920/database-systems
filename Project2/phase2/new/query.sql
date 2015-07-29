CREATE RECURSIVE VIEW R ( d , i , j , p , v ) AS 
(
SELECT 1 , i , j , 1 , v   FROM  M
UNION ALL
SELECT d + 1 ,  R.i , R.j , R.p * M.p , R.v + M.v
FROM R JOIN M ON R . i = M . j 
WHERE d < 6
)
;
SELECT  DISTINCT i, j INTO Q FROM R;
