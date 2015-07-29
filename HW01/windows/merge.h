#include <string>
#include <iostream>
#include <fstream>
#include <sstream>
#include "freqnode.h"

using namespace std;

void merge(FreqNode*, int, int, int, FreqNode*);
void msort(FreqNode*, int, int, FreqNode*);
void read(ifstream&, FreqNode*, int, int&, bool&);
void write(FreqNode*, string, int);
void mergefiles(string, string, string);
void extract(string, string&, int&);

void merge(FreqNode *A, int lpos, int lend, int rend, FreqNode *B)
{
    int rpos = lend + 1;
    int count = lpos;
    int num = rend - lpos + 1;

    while (lpos <= lend || rpos <= rend) {
        if (lpos > lend) { 
            B[count++] = A[rpos++];
        }
        else if (rpos > rend) {
            B[count++] = A[lpos++];
        }
        else if ((A[lpos].word).compare(A[rpos].word) == 0) {
            B[count++] = A[lpos++] + A[rpos++];
            B[count] = B[count-1];
            B[count++].freq = 0;
        }
        else if ((A[lpos].word).compare(A[rpos].word) < 0) {
            B[count++] = A[lpos++];
        }
        else { 
            B[count++] = A[rpos++];
        };
    }

    for (int i = 0; i < num; i++, rend--) {
        A[rend] = B[rend];
    }
}

void msort(FreqNode *A, int l, int r, FreqNode *B)
{
    if (l < r) {
        int m = l + (r-l)/2;
        msort(A, l, m, B);
        msort(A, m+1, r, B);
        merge(A, l, m, r, B);
    }
}

void read(ifstream &file, FreqNode *fq, int sizelimit, int &size, bool &END)
{
    char data; // temp char to hold each letter from input file stream
    string temp1; // temp string to hold each word
    bool VALID = true;
    int count = 0;

    while (!file.eof() && file.good() && count < sizelimit)
    {
        data = file.get();
        if (isspace(data))
        {
            if (!temp1.empty() && VALID) {
                fq[count].freq = 1;
                fq[count++].word = temp1;
            }
            temp1.clear();
            VALID = true;
        }
        else if (isalpha(data))
        {
            temp1.push_back(toupper(data));
            if (file.eof() && VALID) {
                fq[count].freq = 1;
                fq[count++] = temp1;
            }
        }
        else
        {
            if (file.eof() && VALID && !temp1.empty()) {
                fq[count].freq = 1;
                fq[count++] = temp1;
            }
            VALID = false;
            temp1.push_back(data);
        }
    }
    size = count;
    if (file.eof()) END = true;
} 
   
void write(FreqNode *fq, string outname, int size)
{
     ofstream out;
     out.open(outname.c_str(), ofstream::out);

     for (int i = 0; i < size; i++) {
         if (fq[i].freq > 0)
             out << fq[i].word << " " << fq[i].freq << endl;
     }
     out.close();
}

void mergefiles(string in1, string in2, string out)
{
    ifstream infile1(in1.c_str());
    ifstream infile2(in2.c_str());
    ofstream outfile(out.c_str());

    string line1;
    string line2;
    string word1;
    string word2;
    int freq1;
    int freq2;

    getline(infile1,line1);
    getline(infile2,line2);
    extract(line1,word1,freq1);
    extract(line2,word2,freq2);
    while (!infile1.eof() || !infile2.eof()) {
        if (infile1.eof()) {
            outfile << word2 << " " << freq2 << endl;
            getline(infile2,line2);
            extract(line2,word2,freq2);
        }
        else if (infile2.eof()) {
            outfile << word1 << " " << freq1 << endl;
            getline(infile1,line1);
            extract(line1,word1,freq1);
        }
        else if (word1 == word2) {
            outfile << word1 << " " << freq1+freq2 << endl;
            getline(infile1,line1);
            getline(infile2,line2);
            extract(line1,word1,freq1);
            extract(line2,word2,freq2);
        }
        else if (word1 < word2) {
            outfile << word1 << " " << freq1 << endl;
            getline(infile1,line1);
            extract(line1,word1,freq1);
        }
        else {
            outfile << word2 << " " << freq2 << endl;
            getline(infile2,line2);
            extract(line2,word2,freq2);
        }
    }
}
    
void extract(string line, string &word, int &freq)
{
    size_t pos1 = line.find(" ");
    size_t pos2 = line.find("\n");
    word = line.substr(0,pos1);
    istringstream ss(line.substr(pos1+1,pos2-pos1-1));
    ss >> freq;
}
