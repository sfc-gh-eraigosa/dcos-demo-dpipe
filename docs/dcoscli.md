# Introduction

If you do not have a cli setup yet, you can do that [here](https://dcos.io/docs/1.9/cli/install/).

These steps are some quick starts for setting up your dcos client from source releases repo on ***macOS*** for this project.

# steps

- Download the dcos cli from [dcos-cli GitHub repo](https://github.com/dcos/dcos-cli/releases):
   ```
   sudo curl -o /usr/local/bin/dcos --url "https://downloads.dcos.io/binaries/cli/darwin/x86-64/0.5.1/dcos" && \
   sudo chmod +x /usr/local/bin/dcos

   ```

- Configure the cli:
  ```
  dcos config set core.dcos_url http://m1.dcos-demo
  ```
