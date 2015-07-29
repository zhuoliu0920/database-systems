This program is called "groupby", which can compute word frequenciese on a document. It is implemented by two-way external merge sort, which has the advantage that input file can have unlimited size, as well as output file. It works as follows:

1. Input file is read into memory by chunks and then sorted and outputed as some temporary files. Each chunk can store at most 1000 words (repeated words may appear). Program will sorted these 1000 words alphabetically and record their frequencies, then output the result to a temporary file (each row with a word followed by its frequency). Program will do this repeatedlly, until reach the end of the input file. As a result, by the end of this step, there may be lots of temporary sorted output files generated, all of which has maximum size 1000. (Here, in memory, we use merge sort method to sort these 1000 words)

2. Merge temporary output files. 
  First, merge every two neighboring temporary files generated in step 1 into a new temporary file. We can treat this procedure as a disk procedure. Because every time, we only need to extract one word from each file into memory, compare these two words, write one of them or both of them into disk, remove the word(s) from memory, and read the next word(s) from input file. (It is similar to the merge procedure in merge sort, the only difference is one is done in memory, the other is done in disk.) 
  Then, delete the old temporary files.
  Do these repeatedly until we have only one temporary sorted file. Then we can rename it to the desired output name, it will be what we want.

In this program, words from input file are treated as separated by "spaces" (space, new line, etc), only those with "a-z" and "A-Z" are valid words, others will not be read. For example, "good", "BAD" are valid, but "you're", "123how", "why?" are not valid. Moreover, in output file, the words are all in upper case.

All the information and error messages will display on screen. (For example, no input, output files, or invalid files. However, if input file can be correctly opened, but no valid word is in it, then output file is just a empty file.)