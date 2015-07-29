#include "commandlineparser.h"
#include "freqnode.h"
#include "merge.h"
#include <sstream>
#include <stdio.h>

using namespace std;

const int MAX_SIZE = 1000;

int main(int argc, char *argv[])
{
    if (argc == 1) 
    {
        cerr << "Error: no argument is passed." << endl;
        return -1;
    }

    CommandLineParser clp(argv[1],';');
    //use CommandLineParser to get the script name
    char *input = clp.extract("input");
    if (input == NULL)
    {
        cerr << "Error: no input file specified." << endl;
        return -1;
    }

    char *output = clp.extract("output");
    if (output == NULL || *output == '\0')
    {
        cerr << "Error: no output file specified." << endl;
        return -1;
    }

    ifstream infile(input);
    if (!infile.is_open()) {
        cerr << "Error: Input file \'" << input << "\' cannot be opened or does not exist." << endl;
        return -1;
    }

    bool REACHEND = false;
    FreqNode *temp;
    FreqNode *temp2;
    temp = new FreqNode[MAX_SIZE];
    temp2 = new FreqNode[MAX_SIZE];
    int size;
    int k = 1;
    string outname;
    string outnametype[2];
    outnametype[0] = "1tempsortedfile_";
    outnametype[1] = "2tempsortedfile_";
    stringstream tail;
    while (REACHEND == false) {
        read(infile,temp,MAX_SIZE,size,REACHEND);
        msort(temp,0,size-1,temp2);
        tail << k;
        outname = outnametype[0] + tail.str() + ".txt";
        tail.str("");
        cout << "Generating temporary sorted file \'" << outname << "\'... ";
        write(temp,outname,size);
        cout << "Done!" << endl;
        k++;
    }
    delete[] temp;
    delete[] temp2;

    int n;
    int count = 1;
    int newname;
    int oldname;
    string inname1;
    string inname2;
    while (k != 2) {
        n = k-1;
        k = 1;
        newname = count%2;
        oldname = (newname == 0)?1:0;
        while (k <= n/2) {
            tail << k;
            outname = outnametype[newname] + tail.str() + ".txt";
            tail.str("");
            tail << (2*k-1);
            inname1 = outnametype[oldname] + tail.str() + ".txt";
            tail.str("");
            tail << (2*k);
            inname2 = outnametype[oldname] + tail.str() + ".txt";
            tail.str("");

            cout << "Merging files \'" << inname1 << "\' and \'" << inname2 << "\' to file \'" << outname << "\'... ";
            mergefiles(inname1,inname2,outname);
            cout << "Done!" << endl;
            cout << "Removing file \'" << inname1 << "\' and \'" << inname2 << "\'... ";
            remove(inname1.c_str());
            remove(inname2.c_str());
            cout << "Done!" << endl;
            k++;
        }
        if (n%2 != 0) {
            tail << k;
            outname = outnametype[newname] + tail.str() + ".txt";
            tail.str("");
            tail << n;
            inname1 = outnametype[oldname] + tail.str() + ".txt";
            tail.str("");

            cout << "Renaming file \'" << inname1 << "\' to new name\'" << outname << "\'... ";
            rename(inname1.c_str(),outname.c_str());
            cout << "Done!" << endl;
            k++;
        }
    count++;
    }

    cout << "Renaming file \'" << outname << "\' to output file name\'" << output << "\'... ";
    remove(output);
    rename(outname.c_str(),output);
    cout << "Done!" << endl;
   
    return 0;
}
