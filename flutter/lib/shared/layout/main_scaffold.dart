import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';

import 'breakpoints.dart';

/// Main scaffold with fixed bottom navigation using StatefulShellRoute.
/// 
/// This widget wraps the 4 main navigation tabs and uses IndexedStack
/// (via GoRouter's StatefulShellRoute) to preserve page state during navigation.
class MainScaffold extends StatelessWidget {
  const MainScaffold({
    super.key,
    required this.navigationShell,
  });

  /// The navigation shell from GoRouter's StatefulShell Route.
  /// It manages the IndexedStack internally.
  final StatefulNavigationShell navigationShell;

  static const _destinations = <({IconData icon, String label})>[
    (icon: Icons.fingerprint, label: '打卡'),
    (icon: Icons.calendar_month, label: '紀錄'),
    (icon: Icons.edit_note, label: '補打卡'),
    (icon: Icons.person_outline, label: '個人資料'),
  ];

  void _onDestinationSelected(int index) {
    navigationShell.goBranch(
      index,
      // Reset the branch's navigation stack when tapping the same tab
      initialLocation: index == navigationShell.currentIndex,
    );
  }

  @override
  Widget build(BuildContext context) {
    final layout = Breakpoints.fromWidth(MediaQuery.sizeOf(context).width);
    
    final content = SafeArea(
      child: Row(
        children: [
          // Left navigation rail for tablet/desktop
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
                        onTap: () => _onDestinationSelected(i),
                        child: AnimatedContainer(
                          duration: const Duration(milliseconds: 180),
                          curve: Curves.easeOut,
                          width: double.infinity,
                          padding: const EdgeInsets.symmetric(vertical: 14, horizontal: 8),
                          decoration: BoxDecoration(
                            color: i == navigationShell.currentIndex
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
                                color: i == navigationShell.currentIndex
                                    ? Colors.white
                                    : const Color(0xFF4B5563),
                              ),
                              const SizedBox(height: 8),
                              Text(
                                _destinations[i].label,
                                textAlign: TextAlign.center,
                                style: TextStyle(
                                  fontSize: 13,
                                  fontWeight: i == navigationShell.currentIndex
                                      ? FontWeight.w600
                                      : FontWeight.w500,
                                  color: i == navigationShell.currentIndex
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
          
          // Main content area
          Expanded(
            child: Column(
              children: [
                // The current branch's page content
                Expanded(
                  child: Align(
                    alignment: Alignment.topCenter,
                    child: ConstrainedBox(
                      constraints: BoxConstraints(
                        maxWidth: layout == AppLayoutType.mobile ? double.infinity : 960,
                      ),
                      child: navigationShell,
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
      body: content,
      bottomNavigationBar: layout == AppLayoutType.mobile
          ? NavigationBar(
              selectedIndex: navigationShell.currentIndex,
              onDestinationSelected: _onDestinationSelected,
              destinations: _destinations
                  .map((item) => NavigationDestination(
                        icon: Icon(item.icon),
                        label: item.label,
                      ))
                  .toList(),
            )
          : null,
    );
  }
}
