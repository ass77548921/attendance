import 'package:attendance_app/core/models/amendment.dart';
import 'package:attendance_app/features/amendment/ui/amendment_detail_page.dart';
import 'package:attendance_app/shared/layout/app_scaffold.dart';
import 'package:attendance_app/features/amendment/ui/amendment_form_page.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';

Widget _wrapWithSize({required Size size, required Widget child}) {
  return ProviderScope(
    child: MediaQuery(
      data: MediaQueryData(size: size),
      child: MaterialApp(home: child),
    ),
  );
}

void main() {
  testWidgets('mobile layout shows bottom navigation and no navigation rail', (tester) async {
    await tester.pumpWidget(
      _wrapWithSize(
        size: const Size(375, 812),
        child: const AppScaffold(currentIndex: 0, title: '測試', child: SizedBox()),
      ),
    );

    expect(find.byType(NavigationBar), findsOneWidget);
    expect(find.byType(NavigationRail), findsNothing);
  });

  testWidgets('desktop layout shows navigation rail and no bottom navigation', (tester) async {
    await tester.pumpWidget(
      _wrapWithSize(
        size: const Size(1280, 900),
        child: const AppScaffold(currentIndex: 0, title: '測試', child: SizedBox()),
      ),
    );

    expect(find.byType(NavigationRail), findsOneWidget);
    expect(find.byType(NavigationBar), findsNothing);
  });

  testWidgets('amendment form uses max width 600 on wide screens', (tester) async {
    await tester.pumpWidget(
      _wrapWithSize(
        size: const Size(1280, 900),
        child: const AmendmentFormPage(),
      ),
    );

    final constrainedBoxes = tester.widgetList<ConstrainedBox>(find.byType(ConstrainedBox)).toList();
    final formBox = constrainedBoxes.firstWhere(
      (item) => item.constraints.hasBoundedWidth && item.constraints.maxWidth == 600,
    );

    expect(formBox.constraints.maxWidth, 600);
  });

  testWidgets('amendment detail page shows attachment preview cards', (tester) async {
    const amendment = Amendment(
      id: 1,
      userId: 1,
      userFullName: '測試員工',
      targetDate: '2026-04-14',
      amendmentType: AmendmentType.clockIn,
      amendedTime: '2026-04-14T09:00:00Z',
      reason: '補登',
      status: AmendmentStatus.pending,
      reviewNote: null,
      reviewedBy: null,
      reviewedAt: null,
      createdAt: '2026-04-14T09:00:00Z',
      attachments: [
        AmendmentAttachment(
          id: 1,
          originalFilename: 'proof.png',
          mimeType: 'image/png',
          fileSize: 10240,
        ),
      ],
    );

    await tester.pumpWidget(
      _wrapWithSize(
        size: const Size(800, 900),
        child: const SizedBox(),
      ),
    );
    await tester.pumpWidget(
      ProviderScope(
        child: MaterialApp(home: Scaffold(body: Builder(builder: (context) => Theme(data: Theme.of(context), child: AmendmentDetailPage(amendment: amendment))))),
      ),
    );

    expect(find.text('proof.png'), findsOneWidget);
    expect(find.byIcon(Icons.image_outlined), findsOneWidget);
  });
}