/*
 * proxy.c - CS:APP Web proxy
 *
 * TEAM MEMBERS:
 *     Andrew Tillmann, fx2323@gmail.com 
 * 
 * IMPORTANT: Give a high level description of your code here. You
 * must also provide a header comment at the beginning of each
 * function that describes what that function does.
 */ 

#include "csapp.h"
#define MAXTREAD (1000)
#define MIN(a,b) (((a)<(b))?(a):(b))
int avialableTreads[MAXTREAD]; 

/*
 * used to pass request info
 */
struct requestInfo{
  char *request[MAXLINE];
  char *hostname[MAXLINE];
  char *pathname[MAXLINE];
  int  *port;
};
/*
 * used to pass thread info
 */
struct treadInfo{
  int clientfd;
  int serverfd;
  int ID;
  int transferSize;
  rio_t rioWithClient;
  rio_t rioWithServer;
  char  uri[MAXLINE];
  struct sockaddr_in sockaddr;
};


/*
 * Function prototypes
 */
int parse_uri(char *uri, char *target_addr, char *path, int  *port);
void format_log_entry(char *logstring, struct sockaddr_in *sockaddr, char *uri, int size);
void * doit(void * data);
struct requestInfo request(treadInfo *thread, int *portValue);
void transferData(struct threadInfo *thread);
void pthreadExit(treadInfo * thread);
void addStrings(char* output,char* input,treadInfo *thread);
void serverConnection(char * hostname,int port,treadInfo *thread);
ssize_t rio_readn_w(int fd, void *usrbuf, size_t n,threadInfo *thread);
ssize_t rio_readnb_w(rio_t *rp, void *usrbuf, size_t n,threadInfo *thread);
ssize_t rio_readlineb_w(rio_t *rp, void *usrbuf, size_t n,threadInfo *thread);
void rio_writen_w(int fd, void *usrbuf, size_t n,threadInfo *thread);



/* 
 * main - Main routine for the proxy program runs the proxy server
 */
int main(int argc, char **argv)
{
  int listenfd, connfd, clientlen, ID=0;
  struct sockaddr_in clientaddr;
  struct sockaddr_in * passClientaddr=&clientaddr;
  struct hostent *hp;
  char *haddrp;
  pthread_t tid[MAXTREAD];

 
  /* Check arguments */
  if(argc != 2){
    fprintf(stderr, "Usage: %s <port number>\n", argv[0]);
    exit(0);
  }
  signal(SIGPIPE, SIG_IGN);
  listenfd=Open_listenfd(atoi(argv[1]));
  
  while(1){
    
    clientlen =sizeof(clientaddr);
    connfd = Accept(listenfd, (SA *)&clientaddr,(unsigned int *)&clientlen);
    
    //Determine the domain name and IP address of the client then send print statement
    hp=Gethostbyaddr((const char *)&clientaddr.sin_addr.s_addr, sizeof(clientaddr.sin_addr.s_addr), AF_INET);
    haddrp =inet_ntoa(clientaddr.sin_addr);
    printf("Thread %d: Receieved request from %s (%s):\n",ID,hp->h_name,haddrp);

    //make and set known data to thread
    struct treadInfo thread;
    thread.clientfd=connfd;
    rio_readinitb(&thread.rioWithClient,thread.clientfd);
    thread.ID=ID;
    thread.sockaddr=*passClientaddr;
 
    //create new thread
    Pthread_create(&tid[ID], NULL,doit,&(void *)thread); 

    //add one to ID count
    ID++;
  }    
  exit(0);
}


/* 
 * sends the request then it gets data from the outside server then sends to outside client
 */
void* doit(void * data){
  struct treadInfo *thread=(struct doitInfo *)data;
  int   portValue=0;
  int  *port=&portValue;

  //ignore SIGPIPE singal
  signal(SIGPIPE, SIG_IGN);

  //builds the request from client
  struct requestInfo getRequest=request(thread,port);
  
  //connecting to server  if fails closes //need to change to own function
  (*thread).serverfd=serverConnection((char *)getRequest.hostname,*getRequest.port,thread);
  
  //prints out the request will add to request function
  printf("Thread %d: Forwarding request to end server:\n",(*thread).ID);
  printf("%s",(char *)getRequest.request);
  printf("*** End of Request ***\n\n");
  
  //set Rio
  rio_readinitb(&(*thread).rioWithServer,(*thread).serverfd);

  //send request
  rio_writen_w((*thread).serverfd,getRequest.request,strlen((const char *)getRequest.request),thread);

  //transfer recieved data from server to client
  transferData(thread);
  
  //should never reach this exit()
  exit(0);
}


/* 
 * builds the request from the client going to the server
 */
struct requestInfo request(treadInfo *thread, int *portValue){
  int is_static;
  char buf[MAXLINE],method[MAXLINE];
  struct requestInfo output;
  
  //assign *port to portValue
  output.port=&(*portValue);
  
  //reads the frist line of data from the client
  rio_readlineb_w(&rioWithClient,buf1,MAXLINE);
  printf("%s",buf1);
  sscanf(buf1,"%s %s",method,(*thread).uri);

  //prase uri to find hostname, pathname and port
  is_static = parse_uri((*thread).uri,output.hostname,output.pathname,output.port);

  //if prase uri is successful
  if(is_static==0){

    //adds the first line of the  request to requestBuf
    sprintf(requestBuf,"%s /%s HTTP/1.0\r\n",method,output.pathname);
   
    //adds all the next headers of the request
    while(strcmp((const char *)buf,"\r\n")!=0){
      rio_readlineb_w(&(*thread).rioWithClient,buf,MAXLINE,thread);
      printf("%s",buf);
      addString(*output.request,buf,thread);
    }

    //gives output to termial
    printf("*** End of Request ***\n\n");
  }

  //if prase uri not successful exit
  else{pthreadExit(thread);}

  return output;
}


/* 
 * transfers the data recieved from the server to the client
 */
void transferData(struct threadInfo *thread){
  int n,dataNeeded=-1,type=200;
  char * buf[MAXLINE];

  //get headers from server and transfer to client
  while(dataNeeded<0 && type==200){
    strcpy((char *)buf,"");
    while(strcmp((const char *)buf,"\r\n")!=0){
      n=rio_readlineb_w(&(*thread).rioWithServer,buf,MAXLINE,thread);
      (*thread).transferSize+=n;
      printf("Thread %d: Forwarded %d bytes from end server to client\n",(*thread).ID,n);
      rio_writen_w((*thread).clientfd,buf,n,thread);
      sscanf(buf, "Content-Length: %d",&dataNeeded);
      sscanf(buf, "HTTP/1.0 %d",&type);
    }

    // if body transfer the data from server to client
    if(dataNeeded>0){
      while((n=rio_readnb_w(&rioWithServer,buf,MIN(MAXLINE,dataNeeded, thread)))>0){
	(*thread).transferSize+=n;dataNeeded-=n;
	rio_writen_w(clientfd,buf,n,thread);
	printf("Thread %d: Forwarded %d bytes from end server to client\n",(*thread).ID,n);
      }
    }

    //successful exit
    pthreadExit(thread);
  }


  /* 
   * pthreadExit
   */
  void pthreadExit(treadInfo * thread){
    char logEntry[MAXLINE];

    //closes the connections
    if(fcntl((*thread).serverfd,F_GETFD)==0){Close((*thread).serverfd);}
    if(fcntl((*thread).clientfd,F_GETFD)==0){Close((*thread).clientfd);}

    //print log entry
    if((*thread).transferSize>0){
      format_log_entry(logEntry,(*thread).sockaddr,(*thread).uri,(*thread.)size);
      printf("%s",logEntry);
    }
    pthread_exit(NULL);
  }


/* 
 * adds two strings together exits if fails
 */
 void addStrings(char* output,char* input,treadInfo *thread){

   if((strlen(output)+strlen(input))>sizeof(output)){
     *output=(int long)realloc(output,(strlen(output)+strlen(input)));
   }
   if(*output==(int long)NULL){
     printf("Error allocating memory\n");
     pthreadExit(thread);
   }
   sprintf(output,"%s%s",output,intput);
 }

 /*
  * sets serverfd if successful if not exits thread 
  */
 void serverConnection(char * hostname,int port,treadInfo *thread){
   int fd;
     
   //test connection
   if((fcntl((fd=(Open_clientfd(hostname,port))), F_GETFL) !=-1 || errno != EBADF)==1){
     //connection successful
     (*thread).serverfd=fd;
   }
   else{
     //connection faild
     pthreadExit(thread);
   }
 }

 /*
  *  input output functions that exits thread if fail
  */
 ssize_t rio_readn_w(int fd, void *usrbuf, size_t n,threadInfo *thread){
   ssize_t temp=rio_readn(fd,usrbuf,n);
   if(temp<0){printf("rio_readn error\n");}
   return temp;
 }
 ssize_t rio_readnb_w(rio_t *rp, void *usrbuf, size_t n,threadInfo *thread){
   ssize_t temp=rio_readnb(rp,usrbuf,n);
   if(temp<0){printf("rio_readn error\n");}
   return temp;
 }
 void rio_writen_w(int fd, void *usrbuf, size_t n,threadInfo *thread){
   rio_writen(fd,usrbuf,n);
   return 0;
 }
 ssize_t rio_readlineb_w(rio_t *rp, void *usrbuf, size_t n,threadInfo *thread){
   ssize_t temp=rio_readlineb(rp,usrbuf,n);
   if(temp<0){printf("rio_readlineb error\n");}
   return temp;
 }


/*
 * parse_uri - URI parser
 * 
 * Given a URI from an HTTP proxy GET request (i.e., a URL), extract
 * the host name, path name, and port.  The memory for hostname and
 * pathname must already be allocated and should be at least MAXLINE
 * bytes. Return -1 if there are any problems.
 */
int parse_uri(char *uri, char *hostname, char *pathname, int *port)
{
    char *hostbegin;
    char *hostend;
    char *pathbegin;
    int len;
    if (strncasecmp(uri, "http://", 7) != 0) {
	hostname[0] = '\0';
	return -1;
    }
    /* Extract the host name */
    hostbegin = uri + 7;
    hostend = strpbrk(hostbegin, " :/\r\n\0");
    len = hostend - hostbegin;
    strncpy(hostname, hostbegin, len);
    hostname[len] = '\0';
    
    /* Extract the port number */
    *port = 80; /* default */
    if (*hostend == ':'){*port = atoi(hostend + 1);}
    /* Extract the path */
    pathbegin = strchr(hostbegin, '/');
    if (pathbegin == NULL) {
	pathname[0] = '\0';
    }
    
    else {
	pathbegin++;	
	strcpy(pathname, pathbegin);
    }
    return 0;
}


/*
 * format_log_entry - Create a formatted log entry in logstring. 
 * 
 * The inputs are the socket address of the requesting client
 * (sockaddr), the URI from the request (uri), and the size in bytes
 * of the response from the server (size).
 */
void format_log_entry(char *logstring, struct sockaddr_in *sockaddr, 
		      char *uri, int size)
{
    time_t now;
    char time_str[MAXLINE];
    unsigned long host;
    unsigned char a, b, c, d;

    /* Get a formatted time string */
    now = time(NULL);
    strftime(time_str, MAXLINE, "%a %d %b %Y %H:%M:%S %Z", localtime(&now));

    /* 
     * Convert the IP address in network byte order to dotted decimal
     * form. Note that we could have used inet_ntoa, but chose not to
     * because inet_ntoa is a Class 3 thread unsafe function that
     * returns a pointer to a static variable (Ch 13, CS:APP).
     */
    host = ntohl(sockaddr->sin_addr.s_addr);
    a = host >> 24;
    b = (host >> 16) & 0xff;
    c = (host >> 8) & 0xff;
    d = host & 0xff;


    /* Return the formatted log entry string */
    sprintf(logstring, "%s: %d.%d.%d.%d %s %d\n", time_str, a, b, c, d, uri, size);
}
