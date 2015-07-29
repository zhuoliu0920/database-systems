#include "freqnode.h"
#include "merge.h"

int main()
{
    FreqNode* words;
    words = new FreqNode[10];
    ifstream inputfile("input.txt");
    int size;
    bool END = false;
    int sizelimit = 5;
    read(inputfile, words, sizelimit, size, END);
cout << "size is " << size << endl;
cout << "REACH END? " << END << endl;
    for (int i = 0; i < size; i++) {
        cout << words[i].word << " " << words[i].freq << endl;
    }

    read(inputfile, words, sizelimit, size, END);
cout << "size is " << size << endl;
cout << "REACH END? " << END << endl;
    for (int i = 0; i < size; i++) {
        cout << words[i].word << " " << words[i].freq << endl;
    }
    delete[] words;
    return 0;
}
