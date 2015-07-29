#ifndef FREQNODE_H
#define FREQNODE_H

#include <string>
#include <iostream>

using namespace std;

class FreqNode
{
public:
    string word;
    unsigned int freq; // to store the frequency of this word

    FreqNode() {};
    FreqNode(string);
    FreqNode(string,unsigned int);
    ~FreqNode() {}; // destructor
    FreqNode operator+ (const FreqNode&);
};

FreqNode::FreqNode(string input)
{
    word = input;
    freq = 1;
};

FreqNode::FreqNode(string input, unsigned int num)
{
    word = input;
    freq = num;
};

FreqNode FreqNode::operator+ (const FreqNode &rhs)
{
    FreqNode temp(*this);
    temp.freq += rhs.freq;
    return temp;
}

#endif
