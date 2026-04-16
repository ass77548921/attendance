import 'package:flutter/widgets.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'app.dart';
import 'flavors.dart';

void bootstrap(Flavor flavor) {
  WidgetsFlutterBinding.ensureInitialized();
  F.appFlavor = flavor;
  runApp(const ProviderScope(child: AttendanceApp()));
}

void main() {
  bootstrap(Flavor.stage);
}
