load("//toolchains/java:def.bzl", "java_library", "java_binary")

java_library(
    name = "pickachu-bot-lib",
    srcs = glob(["src/pickachu/**/*.java"]),
    deps = [
        "//pickachu-bot/lib:ev3classes",
        "//pickachu-bot/lib:websocket",
        "//pickachu-bot/lib:slf4j-api",
        "//pickachu-bot/lib:slf4j-simple",
    ],
    resources = [
        "//client:files",
        "//pickachu-bot/rsc:sounds"
    ],
)

java_binary(
    name = "pickachu-bot",
    lib = ":pickachu-bot-lib",
    mainclass = "pickachu.bot.PickachuBot"
)
