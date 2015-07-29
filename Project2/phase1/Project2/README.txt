1. Since we test the code under Linux, we use ',' instead of ';' as separators of parameters. For example, the command rq becomes "java rq E=flight,i=from,j=to,v=distance,k=5".

2. We use file "vertica-jdk5-6.1.3-0.jar" as the JDBC library, "Jama-1.0.3.jar" as SVD(to find eigenvalues) library, so when compiling the file, one should use the following command:
"javac -cp .:./vertica-jdk5-6.1.3-0.jar:./Jama-1.0.3.jar rq.java"
And running the program:
"java -cp .:./vertica-jdk5-6.1.3-0.jar:./Jama-1.0.3.jar rq E=E,i=i,j=j,v=v,k=5"

3. For the optional part of finding eigenvalue of Laplacian matrix, one can enable this option by changing the parameter "Laplacian=true" in the file "java.ini". Since the size of this matrix depends on the number of nodes in the graph table, if the size of the graph table is very large, it will be extremely slow to generate Laplacian matrix.

4. I think there is a typo in the pdf file, we should find the 2nd smallest eigenvalue for the Laplacian, not the 2nd largest one, because this number will indicate the connectivity of the graph. Value of the 2nd smallest eigenvalue of Laplacian and circle detecting result will be displayed on screen.

5. In Vertica, after inserting some rows in order into table, when we use SQL query to display them, the order will sometimes change. For example, for the Laplacian, two rows "1,-1" and "-1,1" are inserted into the table "Laplacian" in order to generate the Laplacian matrix:
 1 -1
-1  1
However, sometimes, when we use the SQL query "SELECT * FROM Laplacian", the result may become:
-1  1
 1 -1
The order of these two rows is changed. I am not sure whether the order will be changed if called by JDBC, but I think if we query the table right after we create it, the order will not be changed. (I concern this because if rows of matrix are permutated, the eigenvalue will change)
