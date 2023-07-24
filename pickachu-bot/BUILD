load("@rules_java//java:defs.bzl", "java_binary")

java_binary(
    name = "pickachu-bot",
    srcs = glob(["src/pickachu/**/*.java"]),
    deps = [
        "//pickachu-bot/lib:ev3classes",
        "//pickachu-bot/lib:dbusjava",
        "//pickachu-bot/lib:websocket",
        "//pickachu-bot/lib:slf4j-api",
        "//pickachu-bot/lib:slf4j-simple",
    ],
    resources = [
        "//client:files",
        "//pickachu-bot/rsc:sounds"
    ],
    main_class = "pickachu.bot.PickachuBot"
)
