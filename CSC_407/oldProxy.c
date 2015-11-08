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
int avialableTreads[MAXTREAD]; 

/*
 * Wrapper functions
 */
ssize_t rio_readn_w(int fd, void *usrbuf, size_t n){
  ssize_t temp=rio_readn(fd,usrbuf,n);
  if(temp==-1){printf("rio_readn error\n");}
  return temp;
}
ssize_t rio_writen_w(int fd, void *usrbuf, size_t n){
  ssize_t temp=rio_writen(fd,usrbuf,n);
  if(temp==-1){printf("rio_writen error\n");}
  return temp;
}
ssize_t rio_readlineb_w(rio_t *rp, void *usrbuf, size_t n){
  ssize_t temp=rio_readlineb(rp,usrbuf,n);
  if(temp==-1){printf("rio_readlineb error\n");}
  return temp;
}

/*
 * used to return request function
 */
struct requestInfo{
  char *request[MAXLINE];
  char *hostname[MAXLINE];
  char *pathname[MAXLINE];
  int  *port;
};
/*
 * used to pass info to doit()
 */
struct doitInfo{
  int *avialable;
  int clientfd;
  int thread;
  struct sockaddr_in sockaddr;
};


/*
 * Function prototypes
 */
int parse_uri(char *uri, char *target_addr, char *path, int  *port);
void format_log_entry(char *logstring, struct sockaddr_in *sockaddr, char *uri, int size);

/* 
 * builds the request for step 1
 */
struct requestInfo request(rio_t rioWithClient, int *portValue){
  int n, is_static;
  char buf1[MAXLINE],requestBuf[MAXLINE],method[MAXLINE],uri[MAXLINE];
  char hostname[MAXLINE],pathname[MAXLINE];
  struct requestInfo output;
  //assign *port to portValue
  output.port=&(*portValue);
  
  //reads the frist line of data from the client
  rio_readlineb_w(&rioWithClient,buf1,MAXLINE);
  printf("%s",buf1);
  sscanf(buf1,"%s %s",method,uri);

  //prase uri to find hostname, pathname and port
  is_static = parse_uri(uri,hostname,pathname,output.port);
  //if prase uri is successful
  if(is_static==0){
    //copies hostname
    strcpy((char *)output.hostname,hostname);
    strcpy((char *)output.pathname,pathname);
    //adds the first line of the  request to requestBuf
    sprintf(requestBuf,"%s /%s HTTP/1.0\r\n",method,pathname);
    //adds all the next lines of the request to requestBuf
    memset(buf1,0,sizeof(buf1));
    while((n=rio_readlineb_w(&rioWithClient,buf1,MAXLINE))!=0){
      printf("%s",buf1);
      if(strcasecmp(buf1,"\r\n")==0){break;}
      if((strlen(requestBuf)+strlen(buf1))>sizeof(requestBuf)){
	*requestBuf=(int long)realloc(requestBuf,(strlen(requestBuf)+strlen(buf1)));
	if(*requestBuf==(int long)NULL){printf("Error allocating memory\n");
	  *output.request=NULL;
	  return output;}
      }
      sprintf(requestBuf,"%s%s",requestBuf,buf1); //allHeaders
      memset(buf1,0,sizeof(buf1));
    }
    if((strlen(requestBuf)+strlen("\r\n"))>sizeof(requestBuf)){
      *requestBuf=(int long)realloc(requestBuf,(strlen(requestBuf)+strlen("\r\n")));
      if(*requestBuf==(int long)NULL){printf("Error allocating memory\n");
	*output.request=NULL;
	return output;}
    }
    sprintf(requestBuf,"%s\r\n",requestBuf);
    printf("*** End of Request ***\n\n");
    //copies requestBuf
    if(strlen(requestBuf)>sizeof(output.request)){
      *output.request=realloc(output.request,strlen(requestBuf));
      if(*output.request==NULL){printf("Error allocating memory\n");
	*output.request=NULL;
	return output;}
    }
    strcpy((char *)output.request,requestBuf);
    return output;
  }
  //if prase uri not successufl
  else{printf("parse_uri error\n");}
  *output.request=NULL;
  return output;
}


/* 
 * sends the request then it gets data from the outside server then sends to outside client
 */
void* doit(void * data){
 
  struct doitInfo *info=(struct doitInfo *)data;
  int clientfd=(*info).clientfd;
  int thread=(*info).thread;
  struct sockaddr_in *sockaddr=&(*info).sockaddr;

  int serverfd, n, size=0;
  int portValue=0;
  int *port=&portValue;
  char *buf[MAXLINE], logEntry[MAXLINE], uri[MAXLINE];
  rio_t rioWithClient, rioWithServer;
 

  //set clientfd to rio
  rio_readinitb(&rioWithClient,clientfd);

  //builds the request from client
  struct requestInfo getRequest=request(rioWithClient,port);
  if(*getRequest.request==NULL){
    printf("Thread %d: invailed request\n",thread);
    //closes the connections to client
    Close(clientfd);
    //set tread avialableTreads to free
    *(*info).avialable=0;
    return data;
  }

  //sets the uri
  sprintf((char *)uri,"%s%s",(char *)getRequest.hostname,(char *)getRequest.pathname);

  //prints out the request
  printf("Thread %d: Forwarding request to end server:\n",thread);
  printf("%s",(char *)getRequest.request);
  printf("*** End of Request ***\n\n");

  //connecting to server
  serverfd=Open_clientfd((char *)getRequest.hostname,*getRequest.port);
  rio_readinitb(&rioWithServer,serverfd);

  //send request
  rio_writen_w(serverfd,getRequest.request,strlen((const char *)getRequest.request));

  //receive request from server then send to client
  while((n=rio_readn_w(serverfd,buf,MAXLINE))>0){
    size +=n;
    rio_writen_w(clientfd,buf,n);
    printf("Thread %d: Forwarded %d bytes from end server to client\n",thread,n);
    memset(buf,0,sizeof(buf));
  }
  
  //print log entry
  format_log_entry(logEntry,sockaddr,uri,size);
  printf("%s",logEntry);
  

  //closes the connections to server and client
  Close(serverfd);
  if(serverfd!=clientfd){Close(clientfd);}
  //set tread avialableTreads to free
   *(*info).avialable=0;
  return data; 
}

/* 
 * runs the proxy server
 */
int serverSide(int proxyPort){
  int listenfd, connfd, clientlen, thread=0;
  struct sockaddr_in clientaddr;
  struct sockaddr_in * passClientaddr=&clientaddr;
  struct hostent *hp;
  char *haddrp;
  pthread_t tid[MAXTREAD];

  //sets all avialableTreads values to 0
  int i;
  for(i=0;i<MAXTREAD;i++){avialableTreads[i]=0;}

  listenfd=Open_listenfd(proxyPort);
  while(1){
    clientlen =sizeof(clientaddr);

    connfd = Accept(listenfd, (SA *)&clientaddr,(unsigned int *)&clientlen);
   
    //Determine the domain name and IP address of the client//
    hp=Gethostbyaddr((const char *)&clientaddr.sin_addr.s_addr, sizeof(clientaddr.sin_addr.s_addr), AF_INET);
    haddrp =inet_ntoa(clientaddr.sin_addr);
    
    

    //build data to send to doit
    struct doitInfo info;
    void *data=&info;
    info.clientfd=connfd;
    info.sockaddr=*passClientaddr;
    int find=1;
    while(find>0){
      //create new thread if avialable
      if(avialableTreads[thread]==0){
	printf("Thread %d: Receieved request from %s (%s):\n",thread,hp->h_name,haddrp);
	avialableTreads[thread]=1;
	info.avialable=&(avialableTreads[thread]);
	info.thread=thread;
	Pthread_create(&tid[thread], NULL,doit,data); 
	find=0;
	thread++;
      }
      else{
	//test next thread avialablity
	thread++;
	//restart test list
	if(thread==(MAXTREAD)){thread=0;}
      }

    }
  }
  exit(0);
}

/* 
 * main - Main routine for the proxy program 
 */
int main(int argc, char **argv)
{
    /* Check arguments */
    if(argc != 2){
	fprintf(stderr, "Usage: %s <port number>\n", argv[0]);
	exit(0);
    }
    else{serverSide(atoi(argv[1]));}
    exit(0);
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
