PLAY_HOME=/home/hntoplinks/play-1.2.5
PLAY=$PLAY_HOME/play
HNTOPLINKS_SRC=/home/hntoplinks/hntoplinks
HNTOPLINKS_PROD=/home/hntoplinks/hntoplinks_prod

git pull

cd $HNTOPLINKS_PROD
$PLAY stop $HNTOPLINKS_PROD

rm -Rf $HNTOPLINKS_PROD
mkdir $HNTOPLINKS_PROD
cp -R $HNTOPLINKS_SRC/* $TCOMMERCE_PROD/
rm $HNTOPLINKS_PROD/README.md
rm $HNTOPLINKS_PROD/build.sh
cd $HNTOPLINKS_PROD
$PLAY deps --sync
$PLAY start -Dpidfile.path=$TCOMMERCE_PROD/server.pid --%prod
