"""
Paste the solutions to problems 4.17, 4.18, 4.19, and 4.20 here:
>>> #4.17
>>> message='The secret of thiks message is that it is secret.'
>>> length=len(message)
>>> count=message.count('secret')
>>> censored=message.replace('secret','xxxxxx')

>>> #4.18
>>> s='''It was the best of times, it was the worst of times; it was the age of wisdom, it was the age of foolishness; it was the epoch of belief, it was teh epoch of incredulity; it was ...'''
>>> newS=s.translate('.,,;\n','      ')
>>> table=str.maketrans('.,,;\n','     ')
>>> newS=s.translate(table)
>>> newS=newS.strip()
>>> newS=newS.lower()
>>> newS.count('it was')
7
>>> newS.replace('was','is')
'it is the best of times  it is the worst of times  it is the age of wisdom  it is the age of foolishness  it is the epoch of belief  it is teh epoch of incredulity  it is'
>>> listS=newS.split()

>>> #4.19
>>> first='Marlena'
>>> last='Sigel'
>>> middle='Mae'
>>> print('{}, {} {}'.format(last,first,middle))
Sigel, Marlena Mae
>>> print('{}, {} {:.1}.'.format(last,first,middle))
Sigel, Marlena M.
>>> print('{} {:.1}. {}'.format(first,middle,last))
Marlena M. Sigel
>>> print('{:.1}. {:.1}. {}'.format(first,middle,last))
M. M. Sigel


>>> #4.20
>>> sender='tim@abc.com'
>>> recipient='tom@xyz.org'
>>> subject='Hello!'
>>> print('From: {}\nTo: {}\nSubject: {}'.format(sender,recipient,subject))
From: tim@abc.com
To: tom@xyz.org
Subject: Hello!


DO NOT PASTE BELOW THIS!!!
"""
#4.24
def cheer(team):
    'takes team name and prints you a cheer!'
    #builds the cheer
    cheer=team.replace('',' ')
    cheer=cheer.strip()
    cheer=cheer.upper()
    #prints the corrent format
    print("How do you spell winner?\nI know, I know!\n{} !\nAnd that's how you spell winner!\nGo {}!".format(cheer,team))

#4.25
def vowelCount(text):
    'takes a string as input and counts and prints the number of occurrences of vowels'

    #builds counts of vowels 
    a=text.count('a')+text.count('A')
    e=text.count('e')+text.count('E')
    i=text.count('i')+text.count('I')
    o=text.count('0')+text.count('O')
    u=text.count('u')+text.count('U')

    #prints in corrent format
    print("a, e, i , o, and u appear, respectively, {}, {}, {}, {}, {} times.".format(a,e,i,o,u))
    
#4.26
def crypto(filename):
    'takes file and prints it on the screen with one chagne replace("secret","xxxxxx")'

    #open files and then takes content then closes file
    infile=open(filename,'r')
    content=infile.read()
    infile.close()

    #replaces the word secret with xxxxxx
    content=content.replace('secret','xxxxxx')

    #prints the desired output
    print(content)
    
#4.32
def censor(filename):
    'takes file modifies it and then writes it to new file'
    #please note that this will only work for files using the English alphabet.

    #will use vairables below later...
    letters='ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'
    wordLength=0

    #open files and then takes content then closes file
    infile=open(filename,'r')
    content=infile.read()
    infile.close()

    #goes through all the string looks for just four consecutive English letters if that is the case those letters are replaced with xxxx.
    for i in range(len(content)):
        if content[i] in letters:
            wordLength +=1 # if content[i] is a Enligh letter wordLength is increased by one
            if wordLength==4 and content[i+1] not in letters: #if i plus past 3 content[i] are in letters but not content[i+1] it is a four letter word
                content=content[:i-3]+'xxxx'+content[i+1:]
        else:
            wordLength=0


    #writes output to a file censored.txt
    outfile=open('censored.txt','w')
    outfile.write(content)
    outfile.close()    
