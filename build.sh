#!/bin/sh

buildOneChannel()
{

if [ $# -lt 1 ];then
    echo "      The argument count is less then 1, Please input the correct argument ......"
    exit 0
fi

echo ">>>>>> replace the [[channel]]  to [[$1]] first >>>>>"

#CMD="s/meta-data.*android:value=\".*\".*android:name=\"UMENG_CHANNEL\"/meta-data android:value=\"$1\" android:name=\"UMENG_CHANNEL\"/"
#echo "$CMD"

cat AndroidManifest.xml | sed 's/meta-data.*android:value=\".*\".*android:name=\"UMENG_CHANNEL\"/meta-data android:value=\"'$1'\" android:name=\"UMENG_CHANNEL\"/' > AndroidManifest.xml_tmp

mv AndroidManifest.xml_tmp AndroidManifest.xml

echo "<<<<<<< after replace the [[channel]] >>>>>>>>>"
echo "[[channel]] check the channel for [[$1]] ......"
PARAM_CHANNEL=$1
REAL_CHANNEL=`grep 'android:name="UMENG_CHANNEL"' AndroidManifest.xml | sed 's/.*=\"'$1'\".*/'$1'/'`

if [ "$PARAM_CHANNEL" = "$REAL_CHANNEL" ];then
	echo ">>>>>>>>> check the channel replace success >>>>>>>"
else
	echo ">>>>>>>>> check the channel replace failed >>>>>>>"
	exit 0
fi
sleep 1

demo_version=`grep "android:versionName=" AndroidManifest.xml | sed 's/.*=//' | sed 's/"//' | awk 'BEGIN{FS="\""}{print $1}'`
echo "********* begin build for $1 $demo_version *********"
ant clean ; ant release
echo ""
echo ""
echo ""
echo ""
echo ">>>>>>> after build for linklink >>>>>>>"
sleep 1

if [  $# -gt 1  ];then
    TARGET_DIR=$2
	if [ ! -d $TARGET_DIR ];then
		echo "     target dir : $TARGET_DIR is not exist, just exit ....."
		exit 0
	fi

    echo cp -rf bin/lianliankan-release.apk $TARGET_DIR/lianliankan-release_$demo_version-$1.apk
    cp -rf bin/lianliankan-release.apk $TARGET_DIR/lianliankan_$demo_version-$1.apk
fi

}

buildOneChannel hiapk $1
buildOneChannel gfan $1
buildOneChannel google $1
buildOneChannel mumayi $1
buildOneChannel 360 $1
