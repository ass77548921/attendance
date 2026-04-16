import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';

import 'package:attendance_app/app.dart';
import 'package:attendance_app/flavors.dart';

void main() {
  testWidgets('app boots to login shell when unauthenticated', (WidgetTester tester) async {
    F.appFlavor = Flavor.dev;

    await tester.pumpWidget(const ProviderScope(child: AttendanceApp()));
    await tester.pumpAndSettle();

    expect(find.text('員工登入'), findsOneWidget);
  });
}
