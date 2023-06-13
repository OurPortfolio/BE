REPOSITORY=/firstcicd-ec2/build/libs
cd $REPOSITORY

APP_NAME=firstcicd-ec2
JAR_NAME=$(ls $REPOSITORY | grep 'OurPortfolio-0.0.1-SNAPSHOT.jar' | tail -n 1)
JAR_PATH=$REPOSITORY/$JAR_NAME

sudo chmod +x "$JAR_PATH"

CURRENT_PID=$(pgrep -f $APP_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 종료할것 없음."
else
  echo "> kill -9 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

echo "> $JAR_PATH 배포"
nohup java -jar $JAR_PATH > /dev/null 2> /dev/null < /dev/null &
