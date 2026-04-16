enum Flavor {
  stage,
  dev,
  pro,
}

class F {
  static late final Flavor appFlavor;

  static String get name => appFlavor.name;

  static String get title {
    switch (appFlavor) {
      case Flavor.stage:
        return '打卡(Stage)';
      case Flavor.dev:
        return '打卡(Dev)';
      case Flavor.pro:
        return '打卡';
    }
  }

}
