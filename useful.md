Useful Commands
===============

Play
----
Update dependencies

```
play dependencies --sync
```

SQL
---
Activate subscription

```
update subscription set activated = true, activation_date = subscription_date where activated = false;
```

## Git

Heroku
------
Deploy to Heroku

```
git push heroku master
```

Heroku Logs

```
heroku logs
```

