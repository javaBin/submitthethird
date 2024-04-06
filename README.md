# What is this?

This is an application that lets speakers submit talks to JavaZone.

Integrates with [Moresleep](https://github.com/javaBin/moresleep) which stores the talks.

# Running it in IntelliJ

Submit needs moresleep to run. In a textfile setup values (You will need to get the values from a friend)
```
sleepingPillLocation=<VALUE>
sleepingpillUser=<VALUE>
sleepingpillPassword=<VALUE>
```
Run the class `Webserver`. Supply the setupfile as argument.

## App property files for AWS

To edit which properties are used for deployment to AWS, edit the files in the `config` folder:

```
ansible-vault edit config/<env>.properties.encrypted
```

You need the vault password for this. Ask around to get access to it :)

