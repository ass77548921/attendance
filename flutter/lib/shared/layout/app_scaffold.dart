import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import '../../core/router/app_routes.dart';
import 'breakpoints.dart';

class AppScaffold extends StatelessWidget {
  const AppScaffold({
    super.key,
    required this.child,
    required this.currentIndex,
    this.title,
    this.floatingActionButton,
  });

  final Widget child;
  final int currentIndex;
  final String? title;
  final Widget? floatingActionButton;

  static const _destinations = <({IconData icon, String label, String route})>[
    (icon: Icons.fingerprint, label: '打卡', route: AppRoutes.attendance),
    (icon: Icons.calendar_month, label: '紀錄', route: AppRoutes.attendanceRecords),
    (icon: Icons.edit_note, label: '補打卡', route: AppRoutes.amendments),
    (icon: Icons.person_outline, label: '個人資料', route: AppRoutes.profile),
  ];

  void _go(BuildContext context, int index) {
    context.go(_destinations[index].route);
  }

  @override
  Widget build(BuildContext context) {
    final layout = Breakpoints.fromWidth(MediaQuery.sizeOf(context).width);
    final content = SafeArea(
      child: Row(
        children: [
          if (layout != AppLayoutType.mobile)
            Container(
              width: 136,
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 12),
              child: Column(
                children: [
                  for (var i = 0; i < _destinations.length; i++)
                    Padding(
                      padding: const EdgeInsets.only(bottom: 12),
                      child: InkWell(
                        borderRadius: BorderRadius.circular(22),
                        onTap: () => _go(context, i),
                        child: AnimatedContainer(
                          duration: const Duration(milliseconds: 180),
                          curve: Curves.easeOut,
                          width: double.infinity,
                          padding: const EdgeInsets.symmetric(vertical: 14, horizontal: 8),
                          decoration: BoxDecoration(
                            color: i == currentIndex
                                ? Theme.of(context).colorScheme.primary
                                : Colors.transparent,
                            borderRadius: BorderRadius.circular(22),
                          ),
                          child: Column(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              Icon(
                                _destinations[i].icon,
                                size: 26,
                                color: i == currentIndex
                                    ? Colors.white
                                    : const Color(0xFF4B5563),
                              ),
                              const SizedBox(height: 8),
                              Text(
                                _destinations[i].label,
                                textAlign: TextAlign.center,
                                style: TextStyle(
                                  fontSize: 13,
                                  fontWeight: i == currentIndex
                                      ? FontWeight.w600
                                      : FontWeight.w500,
                                  color: i == currentIndex
                                      ? Colors.white
                                      : const Color(0xFF4B5563),
                                  height: 1.2,
                                ),
                              ),
                            ],
                          ),
                        ),
                      ),
                    ),
                ],
              ),
            ),
          Expanded(
            child: Column(
              children: [
                if (title != null)
                  Padding(
                    padding: const EdgeInsets.fromLTRB(24, 20, 24, 8),
                    child: Row(
                      children: [
                        Expanded(
                          child: Text(title!, style: Theme.of(context).textTheme.headlineSmall),
                        ),
                      ],
                    ),
                  ),
                Expanded(
                  child: Align(
                    alignment: Alignment.topCenter,
                    child: ConstrainedBox(
                      constraints: BoxConstraints(maxWidth: layout == AppLayoutType.mobile ? double.infinity : 960),
                      child: child,
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );

    return Scaffold(
      floatingActionButton: floatingActionButton,
      body: content,
      bottomNavigationBar: layout == AppLayoutType.mobile
          ? NavigationBar(
              selectedIndex: currentIndex,
              onDestinationSelected: (index) => _go(context, index),
              destinations: _destinations
                  .map((item) => NavigationDestination(icon: Icon(item.icon), label: item.label))
                  .toList(),
            )
          : null,
    );
  }
}