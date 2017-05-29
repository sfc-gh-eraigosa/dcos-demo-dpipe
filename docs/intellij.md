# Introduction

[Intellij](https://www.jetbrains.com/idea/) is a great development tool for creating scala applications and testing flink.  You can also run it in a container.  

# Quick example for running Intellij in a container

```
intellij(){
  set -x
      docker run -d \
          -e DISPLAY=$DISPLAY \
          -e "TZ=America/Chicago" \
          -v $HOME/git/.intellij:/root/.IdeaIC15 \
          -v $HOME:/workspace/docker \
          -v $HOME/.m2:/root/.m2 \
          --name intellij \
          psharkey/intellij
}

```

Now just execute `intellij` function in a shell.

Learn about this project [here](https://github.com/psharkey/docker/tree/master/intellij).
