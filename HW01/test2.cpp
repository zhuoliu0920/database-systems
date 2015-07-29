#include <string>
#include <fstream>
#include <iostream>

using namespace std;

int main()
{
    ifstream input("1.txt");
    string temp;
    getline(input,temp);
    cout << input.eof() << endl;
    getline(input,temp);
    cout << input.eof() << endl;
    return 0;
}
