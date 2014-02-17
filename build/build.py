#!/usr/bin/python
#-*- coding: utf-8 -*-

import os
import sys
import re
import optparse
import myLib
import time

CHECK_FILE = ['AndroidManifest.xml', 'res', 'src', 'src_lib']
FILE_SUBFIX = ['.java', '.xml']
MENIFEST_FILE = 'AndroidManifest.xml'

STRING_FILE = 'res/values/strings.xml'
BUILD_RES_DIR = 'build_res/'
ASSETS_DIR = 'assets/'
ICON_RES_PATH = 'res/drawable-xhdpi/'
CONFIG_FILE = 'src/com/xstd/qm/Config.java'
LIB_CONFIGL_FILE = 'src_lib/mcuslib/src/com/plugin/common/utils/UtilsConfig.java'

init_optprarse = optparse.OptionParser(usage='python build.py [-d debug] [-c channel_code] [-t target_save] [-f force start day] [-p new package name]')
init_optprarse.add_option('-d', '--debug', dest='debug')
init_optprarse.add_option('-t', '--targetPath', dest='target')
init_optprarse.add_option('-c', '--channel', dest='channel')
init_optprarse.add_option('-f', '--force', dest='forceday')
init_optprarse.add_option('-p', '--package', dest='packageName')

class ARGUMENTS_ERROR(Exception):
    """ replace text failure
    """

class RES_ERROR(Exception):
    """ build resource error
    """

#根据Menifest来获取现在的packageName
def __getPackageName():
    if os.path.exists(MENIFEST_FILE):
        with open(MENIFEST_FILE, 'r') as mfile:
            for line in mfile:
                m = re.search('package=\".*\"', line)
                if m:
                    oldStr = m.group(0)
                    #print oldStr + ' left index = ' + str(oldStr.find('\"')) + ' right index = ' + str(oldStr.rfind('\"'))
                    return oldStr[oldStr.find('\"') + 1:oldStr.rfind('\"')]

    return None

#更具Menifest获取当前的VersionName
def __getVersionName():
    if os.path.exists(MENIFEST_FILE):
        with open(MENIFEST_FILE, 'r') as file:
            for line in file:
                m = re.search('android:versionName=\".*\"', line)
                if m:
                    oldStr = m.group(0)
                    return oldStr[oldStr.find('\"') + 1:oldStr.rfind('\"')]
    return None
#替换filename中的文案。如果filename是文件，直接替换，如果filename是文件夹
#递归替换filename文件夹下的所有文件
def __walk_replace_file(filename, old, new):
    if filename == None or len(filename) == 0:
        raise ARGUMENTS_ERROR()

    if os.path.isfile(filename):
        if __check_file_extend(filename):
            print 'find one file can replace, file : %s' % filename
            if filename != 'Config.java':
                old_replace = old
                new_replace = new
                if filename.endswith('.java'):
                    old_replace = old + '.R'
                    new_replace = new + '.R'
                myLib.replce_text_in_file(filename, old_replace, new_replace)
    elif os.path.isdir(filename):
        wpath = os.walk(filename)
        for item in wpath:
            files = item[2]
            parentPath = item[0]
            for f in files:
                if __check_file_extend(f):
                    #注意，Config文件比较特殊，不做替换
                    if f != 'Config.java':
                        print 'find one file can replace, file : %s/%s' % (parentPath, f)
                        old_repalce = old
                        new_replace = new
                        if f.endswith('.java'):
                            old_replace = old + '.R'
                            new_replace = new + '.R'

                        print 'old = %s and new = %s' % (old, new)
                        myLib.replce_text_in_file('%s/%s' % (parentPath, f), old_replace, new_replace)
    
    return True
                
#检查当前文件是否是.java 和 .xml文件
def __check_file_extend(filename):
    for end in FILE_SUBFIX:
        if filename.endswith(end):
            return True
    return False

def __replace_package_name(new_package_name):
    if new_package_name == None or len(new_package_name) == 0:
        raise ARGUMENTS_ERROR()

    old_package = __getPackageName()

    print '[[replace.py]] try to replace old package : %s to new pacakge : %s' % (old_package, new_package_name)
    for item in CHECK_FILE:
        __walk_replace_file(item, old_package, new_package_name)

    return True

def __onceBuild(debug, channel, target, forceday, packageName):

    print '//' + '*' * 30
    print '|| begin once build for channel:%s to %s' %(channel, target)
    print '\\' + '*' * 30

    if packageName != None:
        __replace_package_name(packageName)

    if forceday != None:
        myLib.replce_text_in_file(CONFIG_FILE, 'FORCE_START_DAY\ =.*;', 'FORCE_START_DAY = %s;' % forceday)

    if debug == 'false':
        myLib.replce_text_in_file(CONFIG_FILE, 'DEBUG\ =.*;', 'DEBUG = %s;' % 'false')
        myLib.replce_text_in_file(LIB_CONFIGL_FILE, 'UTILS_DEBUG\ =.*;', 'UTILS_DEBUG = %s;' % 'false')

    if channel != None:
        myLib.replce_text_in_file(CONFIG_FILE, 'CHANNEL_CODE\ =.*;', 'CHANNEL_CODE = \"%s\";' % channel)
        myLib.replce_text_in_file(STRING_FILE, 'channel_code.*>', 'channel_code">%s</string>' % channel)

    print '='*20 + ' build prepare finish ' + '='*20
    print 'begin build now'
    os.system('ant clean ; ant release')

    if os.path.exists('bin/QuickSetting-release.apk') and target != None:
        if not os.path.exists(target):
            os.mkdirs(target)

        version_name = __getVersionName()
        new_package = __getPackageName()
        target_apk_file = '%s_%s_%s_%s_%s.apk' % ('lianliankan', new_package, version_name, channel, time.strftime("%Y-%m-%d-%H-%M", time.localtime()))
        os.system('cp -rf bin/lianliankan-release.apk %s/%s' % (target, target_apk_file))

        print 'backup the build target %s/%s success >>>>>>>>' % (target, target_apk_file)

    print 'after build for channel : %s, just reset code ' % channel
    os.system('git add .')
    os.system('git reset --hard HEAD')

    print '-' * 40
    print '-' * 40

def __main(args):
    opt, arg = init_optprarse.parse_args(args)
    debug = opt.debug
    target = opt.target
    channel = opt.channel
    forceday = opt.forceday
    packageName = opt.packageName

    if target == None or channel == None:
        raise ARGUMENTS_ERROR()

    __onceBuild(debug, channel, target, forceday, packageName)

    return None

if __name__ == '__main__':
    __main(sys.argv[1:])

