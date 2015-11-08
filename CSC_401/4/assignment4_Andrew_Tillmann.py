#5.15
def vowels(s):
    'takes as input a string and prints the indexes of all vowels'
    vowel_letters='AEIOUaeiou'
    for i in range(len(s)):
        if s[i] in vowel_letters:
            print(i)
        
#5.16
def indexes(word, char):
    '''takes as input a word and a one-character letter and returns a list of indexes
        at which the letter occurs in the word'''
    output_list=list()
    #builds the list
    for i in range(len(word)):
        if word[i]==char:
            output_list.append(i)
    #returns the list
    return output_list

#5.18
def four_letter(wordList):
    'takes as input a list of words and returns the sublist of all four letter words in the list'
    output_list=list()
    #builds the list
    for word in wordList:
        if len(word)==4:
            output_list.append(word)
    #returns the list
    return output_list

#5.26
def rps(play1, play2):
    'simulate game rock, paper, scissors'
    #dictionary(keys are strings(play1+play2) values are outputs(-1,0,1))
    dictionary={'RS':1, 'SP':1, 'PR':1, 'SR':-1, 'PS':-1, 'RP':-1, 'RR':0, 'SS':0, 'PP':0}
    key=play1+play2
    return dictionary[key]

#5.35
def pixels(image):
    'takes two-dimensional list of nonnegative integers and count of positive entries'
    #start count at 0
    count=0
    
    for i in range(len(image)): #image[i] elements in list
        for j in range(len(image[i])): #image[i][j] elements in sublist
            if image[i][j]>0:
                count +=1 #adds one to the count
    return count

    pass

#5.39
def exclamation(phrase):
    'takes a sting and returns it with modification of vowels repeating themselves four times'

    vowel_letters='AEIOUaeiou'
    adjust=0 #will use to adjust i since program is adding to phrase length

    for i in range(len(phrase)):
        i=i+adjust #need the adjust to make sure vowels added are not looked over on the if statement below
        if phrase[i] in vowel_letters:
            phrase=phrase[:i+1]+phrase[i]*3+phrase[i+1:]
            adjust +=3 #adjust the i for chaning the length by pharase[i]*3
            
    phrase=phrase+'!' #need to add ! to the end of string
    #returns phrase
    return phrase
