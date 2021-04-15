# cs1660-project
# Goals:
- Java application implementation and execution on Docker.
- Docker to GCP Cluster communication.
- Inverted indexing MapReduce implementation and execution on the GCP cluster.
- Term and Top-N search algorithms.

# Items accomplished:
- Java application implementation and execution on Docker.
- Docker to GCP Cluster communication.
- Inverted indexing MapReduce implementation and execution on the GCP cluster (MANUALLY).
- Code walkthrough video: https://youtu.be/Q_PtRXyiU4s
- Application demonstration video: 

# Important notes and unresolved errors:
- My application can successfully send Hadoop jobs to the GCP cluster, but the delivered job unfortunately fails without much explanation. 
  + Due to difficulties with tracing bugs on GCP's log records page, I was unable to figure out why the job failed on the cluster.
    However, my videos below demonstrate walkthroughs of the inverted indexing algorithm code and show that the job is executed successfully
    whenever it is done manually on the cloud shell.
- Because of these errors when sending a job to the cluster through my application, I was unable to implement features for term and top-N searching.
  + For the sake of detail, I have included a top-N MapReduce program that can be successfully executed on the GCP cluster when done manually
    on the shell.  This is also demonstrated in my videos.
    
# Execution:
- Note: Execution instructions assume the user is on Mac.
- You should install socat in order for the display to connect to X11 seamlessly.  I recommend installing it through Homebrew.
  + Homebrew installation: 
    ```
    /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
    ```
  + Follow the instructions to add Homebrew to your bash profile to finalize the installation.
  + Next, install socat:
    ```
    brew install socat
    ```
  + Make sure to verify that socat is installed on your machine.
- BEFORE YOU RUN THE IMAGE:
  + In a separate terminal, enter the command:
    ```
    socat TCP-LISTEN:6000,reuseaddr,fork UNIX-CLIENT:\"$DISPLAY\"
    ```
- If running directly from DockerHub:
  1. ```
     docker run -it -e DISPLAY=$(ipconfig getifaddr en0):0 -v /tmp/.X11-unix:/tmp/.X11-unix ajgreen630/cs1660-project:latest
     ```
- If running after pulling from GitHub:
  1. ```
     docker build -t <insert tag> .
     ```
  2. ```
     - docker run -it -e DISPLAY=$(ipconfig getifaddr en0):0 -v /tmp/.X11-unix:/tmp/.X11-unix <tag>:latest
     ```
**If you are using your own credentials JSON file, I strongly recommend building your own image off of the files from GitHub.  Make sure your credentials file is named "credentials.json" in the folder that you are building your image.  The Dockerfile uses cs1660-mapreduce.jar, all of the files in the data folder (so, make sure to have a folder within your image folder called "data" with the files you would want to use in MapReduce before you build the image, and credentials.json.  I will be revoking my key and removing my own cluster VM instances so as to avoid GCP account violations.**
