DROP TABLE IF EXISTS T;

SELECT "a","b","value"
INTO T
FROM graph
WHERE 1=2;

ALTER TABLE T
ADD COLUMN p int;

INSERT INTO T
SELECT "a","b",MIN("value"),COUNT(*)
FROM graph
GROUP BY "a","b";

DROP TABLE IF EXISTS R;

SELECT *
INTO R
FROM T
WHERE 1=2;

ALTER TABLE R
ADD COLUMN d int;

INSERT INTO R
SELECT "a","b","value",p,1
FROM T
WHERE "a" <> "b";

SELECT MAX(d)
FROM R;

INSERT INTO R
SELECT "a","b",MIN("value"),SUM(p),MIN(d)
FROM (
SELECT r1."a" as "a", t1."b" as "b", r1."value"+t1."value" as"value", r1.p*t1.p as p, d+1 as d
FROM (
SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R)) AS r1
INNER JOIN T AS t1
ON r1."b" = t1."a") AS tmp
GROUP BY "a","b";

SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"= "b";

DELETE FROM R
WHERE "a" = "b" OR ("a","b","value") NOT IN (
SELECT "a","b",MIN("value")
FROM R
GROUP BY "a","b");

DELETE FROM R
WHERE (d,"a","b") NOT IN (
SELECT MIN(d),"a","b"
FROM R
GROUP BY "a","b");

SELECT MAX(d)
FROM R;

INSERT INTO R
SELECT "a","b",MIN("value"),SUM(p),MIN(d)
FROM (
SELECT r1."a" as "a", t1."b" as "b", r1."value"+t1."value" as"value", r1.p*t1.p as p, d+1 as d
FROM (
SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R)) AS r1
INNER JOIN T AS t1
ON r1."b" = t1."a") AS tmp
GROUP BY "a","b";

SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"= "b";

DELETE FROM R
WHERE "a" = "b" OR ("a","b","value") NOT IN (
SELECT "a","b",MIN("value")
FROM R
GROUP BY "a","b");

DELETE FROM R
WHERE (d,"a","b") NOT IN (
SELECT MIN(d),"a","b"
FROM R
GROUP BY "a","b");

SELECT MAX(d)
FROM R;

INSERT INTO R
SELECT "a","b",MIN("value"),SUM(p),MIN(d)
FROM (
SELECT r1."a" as "a", t1."b" as "b", r1."value"+t1."value" as"value", r1.p*t1.p as p, d+1 as d
FROM (
SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R)) AS r1
INNER JOIN T AS t1
ON r1."b" = t1."a") AS tmp
GROUP BY "a","b";

SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"= "b";

DELETE FROM R
WHERE "a" = "b" OR ("a","b","value") NOT IN (
SELECT "a","b",MIN("value")
FROM R
GROUP BY "a","b");

DELETE FROM R
WHERE (d,"a","b") NOT IN (
SELECT MIN(d),"a","b"
FROM R
GROUP BY "a","b");

SELECT MAX(d)
FROM R;

INSERT INTO R
SELECT "a","b",MIN("value"),SUM(p),MIN(d)
FROM (
SELECT r1."a" as "a", t1."b" as "b", r1."value"+t1."value" as"value", r1.p*t1.p as p, d+1 as d
FROM (
SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R)) AS r1
INNER JOIN T AS t1
ON r1."b" = t1."a") AS tmp
GROUP BY "a","b";

SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"= "b";

DELETE FROM R
WHERE "a" = "b" OR ("a","b","value") NOT IN (
SELECT "a","b",MIN("value")
FROM R
GROUP BY "a","b");

DELETE FROM R
WHERE (d,"a","b") NOT IN (
SELECT MIN(d),"a","b"
FROM R
GROUP BY "a","b");

SELECT MAX(d)
FROM R;

INSERT INTO R
SELECT "a","b",MIN("value"),SUM(p),MIN(d)
FROM (
SELECT r1."a" as "a", t1."b" as "b", r1."value"+t1."value" as"value", r1.p*t1.p as p, d+1 as d
FROM (
SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R)) AS r1
INNER JOIN T AS t1
ON r1."b" = t1."a") AS tmp
GROUP BY "a","b";

SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"= "b";

DELETE FROM R
WHERE "a" = "b" OR ("a","b","value") NOT IN (
SELECT "a","b",MIN("value")
FROM R
GROUP BY "a","b");

DELETE FROM R
WHERE (d,"a","b") NOT IN (
SELECT MIN(d),"a","b"
FROM R
GROUP BY "a","b");

SELECT MAX(d)
FROM R;

INSERT INTO R
SELECT "a","b",MIN("value"),SUM(p),MIN(d)
FROM (
SELECT r1."a" as "a", t1."b" as "b", r1."value"+t1."value" as"value", r1.p*t1.p as p, d+1 as d
FROM (
SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R)) AS r1
INNER JOIN T AS t1
ON r1."b" = t1."a") AS tmp
GROUP BY "a","b";

SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"= "b";

DELETE FROM R
WHERE "a" = "b" OR ("a","b","value") NOT IN (
SELECT "a","b",MIN("value")
FROM R
GROUP BY "a","b");

DELETE FROM R
WHERE (d,"a","b") NOT IN (
SELECT MIN(d),"a","b"
FROM R
GROUP BY "a","b");

SELECT MAX(d)
FROM R;

INSERT INTO R
SELECT "a","b",MIN("value"),SUM(p),MIN(d)
FROM (
SELECT r1."a" as "a", t1."b" as "b", r1."value"+t1."value" as"value", r1.p*t1.p as p, d+1 as d
FROM (
SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R)) AS r1
INNER JOIN T AS t1
ON r1."b" = t1."a") AS tmp
GROUP BY "a","b";

SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"= "b";

DELETE FROM R
WHERE "a" = "b" OR ("a","b","value") NOT IN (
SELECT "a","b",MIN("value")
FROM R
GROUP BY "a","b");

DELETE FROM R
WHERE (d,"a","b") NOT IN (
SELECT MIN(d),"a","b"
FROM R
GROUP BY "a","b");

SELECT MAX(d)
FROM R;

INSERT INTO R
SELECT "a","b",MIN("value"),SUM(p),MIN(d)
FROM (
SELECT r1."a" as "a", t1."b" as "b", r1."value"+t1."value" as"value", r1.p*t1.p as p, d+1 as d
FROM (
SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R)) AS r1
INNER JOIN T AS t1
ON r1."b" = t1."a") AS tmp
GROUP BY "a","b";

SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"= "b";

DELETE FROM R
WHERE "a" = "b" OR ("a","b","value") NOT IN (
SELECT "a","b",MIN("value")
FROM R
GROUP BY "a","b");

DELETE FROM R
WHERE (d,"a","b") NOT IN (
SELECT MIN(d),"a","b"
FROM R
GROUP BY "a","b");

SELECT MAX(d)
FROM R;

INSERT INTO R
SELECT "a","b",MIN("value"),SUM(p),MIN(d)
FROM (
SELECT r1."a" as "a", t1."b" as "b", r1."value"+t1."value" as"value", r1.p*t1.p as p, d+1 as d
FROM (
SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R)) AS r1
INNER JOIN T AS t1
ON r1."b" = t1."a") AS tmp
GROUP BY "a","b";

SELECT *
FROM R
WHERE d = (SELECT MAX(d) FROM R) AND "a"= "b";

DELETE FROM R
WHERE "a" = "b" OR ("a","b","value") NOT IN (
SELECT "a","b",MIN("value")
FROM R
GROUP BY "a","b");

DELETE FROM R
WHERE (d,"a","b") NOT IN (
SELECT MIN(d),"a","b"
FROM R
GROUP BY "a","b");

SELECT MAX(d)
FROM R;

