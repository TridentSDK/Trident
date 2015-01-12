#!/bin/sh

cd src/test/jcstress
mvn clean install -pl tests-custom -am
java -XX:+UnlockDiagnosticVMOptions -XX:+WhiteBoxAPI -XX:-RestrictContended -jar tests-custom/target/jcstress.jar -c 16 -m tough -r results/tough -yield true
java -XX:+UnlockDiagnosticVMOptions -XX:+WhiteBoxAPI -XX:-RestrictContended -jar tests-custom/target/jcstress.jar -c 16 -m stress -r results/stress -yield true


# 16 threads
# iterations
# time (higher = better lower = faster)
# verbose (print a lot) -v
# deoptimize interval (deopt every x, lower = better higher = faster)
# mode (sanity, quick, default, tough, stress) (may be preset, don't apply for flags)
# stride (lower = better, higher = less overhead)
# -r result output directory
# best:
# java -jar tests-custom/target/jcstress.jar -c 16 -f 10 -iter 1000000000 -time 5000 -deoptRatio 0 -yield true -minStride 0 -maxStride 0 -r /results/limit
#
# preferred:
# java -jar tests-custom/target/jcstress.jar -c 16 -f 10 -iter 1000000 -time 100 -deoptRatio 0 -yield true -minStride 0 -maxStride 0 -r -r /results/pref