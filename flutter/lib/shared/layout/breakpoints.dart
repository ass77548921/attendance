import 'package:flutter/widgets.dart';

enum AppLayoutType { mobile, tablet, desktop }

class Breakpoints {
  Breakpoints._();

  static AppLayoutType fromWidth(double width) {
    if (width < 600) return AppLayoutType.mobile;
    if (width <= 1200) return AppLayoutType.tablet;
    return AppLayoutType.desktop;
  }

  static bool isMobile(BuildContext context) => fromWidth(MediaQuery.sizeOf(context).width) == AppLayoutType.mobile;

  static bool isTablet(BuildContext context) => fromWidth(MediaQuery.sizeOf(context).width) == AppLayoutType.tablet;

  static bool isDesktop(BuildContext context) => fromWidth(MediaQuery.sizeOf(context).width) == AppLayoutType.desktop;
}