# Progress so far

On x32 I run it with:
```
$ time unzip -p ip_addresses.zip ip_addresses | head -30000000 | pv | java -Xmx1500m InetAddrParser.java

Note: InetAddrParser.java uses unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.
 406MiB 0:05:39 [ 1.2MiB/s] [ <=>                             ]
Unique addresses count: 30000000

real    5m40.726s
user    0m6.841s
sys     0m6.091s
```

Optimization is: every IP address cast as `int32 unsigned`.

# Task's requirements' bug

The link found in the task's requirements as of `e331152` commit  does not work properly
```
$ wget -c -t 999 -T 100 'https://ecwid-vgv-storage.s3.eu-central-1.amazonaws.com/ip_addresses.zip'                             
--2021-03-05 21:38:11--  https://ecwid-vgv-storage.s3.eu-central-1.amazonaws.com/ip_addresses.zip                              
Resolving ecwid-vgv-storage.s3.eu-central-1.amazonaws.com (ecwid-vgv-storage.s3.eu-central-1.amazonaws.com)... 52.219.74.156   
Connecting to ecwid-vgv-storage.s3.eu-central-1.amazonaws.com (ecwid-vgv-storage.s3.eu-central-1.amazonaws.com)|52.219.74.156|:
443... connected.                                                                                                              
HTTP request sent, awaiting response... 200 OK                                                                                 
Length: 2147483647 (2.0G) [application/zip]                                                                                    
Saving to: ‘ip_addresses.zip’                                                                                                  
                                                                                                                               
ip_addresses.zip                100%[======================================================>]   2.00G  4.39MB/s    in 6m 32s   
                                                                                                                               
2021-03-05 21:44:44 (5.23 MB/s) - ‘ip_addresses.zip’ saved [2147483647/2147483647]                                             
```
The file downloaded is unusable as both the PKWare's and InfoZIP's format assume to keep the file list at the end of the ZIP archive. There is no any list of archived files at the end of it. It's explainable as the claimed file size was ~20GB.

After the file got deleted and download started again and interrupted, it can be requested from web server with an offset HTTP header to skip the bytes downloaded previously. This is the only way the user can download it at full. The file downloaded is usable afterwards.
```
$ wget -c -t 999 -T 100 'https://ecwid-vgv-storage.s3.eu-central-1.amazonaws.com/ip_addresses.zip'                             
--2021-03-05 22:49:25--  https://ecwid-vgv-storage.s3.eu-central-1.amazonaws.com/ip_addresses.zip                              
Resolving ecwid-vgv-storage.s3.eu-central-1.amazonaws.com (ecwid-vgv-storage.s3.eu-central-1.amazonaws.com)... 52.219.72.225   
Connecting to ecwid-vgv-storage.s3.eu-central-1.amazonaws.com (ecwid-vgv-storage.s3.eu-central-1.amazonaws.com)|52.219.72.225|:
443... connected.                                                                                                              
HTTP request sent, awaiting response... 200 OK                                                                                 
Length: 2147483647 (2.0G) [application/zip]                                                                                    
Saving to: ‘ip_addresses.zip’                                  
                                                                                                                               
ip_addresses.zip      0%[                    ]   6.96M  1.78MB/s    eta 21m 18s                                                
                                                                                                                               
$ wget -c -t 999 -T 100 'https://ecwid-vgv-storage.s3.eu-central-1.amazonaws.com/ip_addresses.zip'                             
--2021-03-05 22:49:33--  https://ecwid-vgv-storage.s3.eu-central-1.amazonaws.com/ip_addresses.zip                              
Resolving ecwid-vgv-storage.s3.eu-central-1.amazonaws.com (ecwid-vgv-storage.s3.eu-central-1.amazonaws.com)... 52.219.74.136   
Connecting to ecwid-vgv-storage.s3.eu-central-1.amazonaws.com (ecwid-vgv-storage.s3.eu-central-1.amazonaws.com)|52.219.74.136|:
443... connected.                                                                                                              
HTTP request sent, awaiting response... 206 Partial Content                                                                    
Length: 20333775140 (19G), 20326228724 (19G) remaining [application/zip]                                                       
Saving to: ‘ip_addresses.zip’                                  
                                                                                                                               
ip_addresses.zip    100%[===================>]  18.94G  3.88MB/s    in 63m 58s                                                 
                               
2021-03-05 23:53:34 (5.05 MB/s) - ‘ip_addresses.zip’ saved [20333775140/20333775140]
```
This could be done better if the user could reproduce the test sequence by the (small I think) piece of software supplied instead of the hosted file itself. It's enough to document the file for applicant to reproduce with its length and a checksum.
