import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../features/amendment/ui/amendment_detail_page.dart';
import '../../features/amendment/ui/amendment_form_page.dart';
import '../../features/amendment/ui/amendment_list_page.dart';
import '../../features/attendance/ui/attendance_home_page.dart';
import '../../features/attendance/ui/attendance_records_page.dart';
import '../../features/auth/state/auth_provider.dart';
import '../../features/auth/ui/change_password_page.dart';
import '../../features/auth/ui/login_page.dart';
import '../../features/profile/ui/edit_profile_page.dart';
import '../../features/profile/ui/profile_page.dart';
import '../models/user_profile.dart';
import '../../shared/layout/main_scaffold.dart';
import '../models/amendment.dart';
import 'app_routes.dart';

String? resolveAuthRedirect({required AuthState auth, required String location}) {
  final onLogin = location == AppRoutes.login;
  final onChangePassword = location == AppRoutes.changePassword;

  if (!auth.isAuthenticated) {
    return onLogin ? null : AppRoutes.login;
  }

  if (auth.mustChangePassword && !onChangePassword) {
    return AppRoutes.changePassword;
  }

  if (!auth.mustChangePassword && (onLogin || location == '/')) {
    return AppRoutes.attendance;
  }

  return null;
}

final appRouterProvider = Provider<GoRouter>((ref) {
  final auth = ref.watch(authProvider);

  return GoRouter(
    initialLocation: AppRoutes.attendance,
    redirect: (context, state) => resolveAuthRedirect(auth: auth, location: state.matchedLocation),
    routes: [
      GoRoute(path: '/', redirect: (context, state) => AppRoutes.attendance),
      GoRoute(path: AppRoutes.login, builder: (context, state) => const LoginPage()),
      GoRoute(path: AppRoutes.changePassword, builder: (context, state) => const ChangePasswordPage()),
      
      // Main shell route with fixed bottom navigation
      StatefulShellRoute.indexedStack(
        builder: (context, state, navigationShell) {
          return MainScaffold(navigationShell: navigationShell);
        },
        branches: [
          // Branch 0: Attendance
          StatefulShellBranch(
            routes: [
              GoRoute(
                path: AppRoutes.attendance,
                builder: (context, state) => const AttendanceHomePage(useScaffold: false),
              ),
            ],
          ),
          
          // Branch 1: Records
          StatefulShellBranch(
            routes: [
              GoRoute(
                path: AppRoutes.attendanceRecords,
                builder: (context, state) => const AttendanceRecordsPage(useScaffold: false),
              ),
            ],
          ),
          
          // Branch 2: Amendments  
          StatefulShellBranch(
            routes: [
              GoRoute(
                path: AppRoutes.amendments,
                builder: (context, state) => const AmendmentListPage(useScaffold: false),
                routes: [
                  GoRoute(
                    path: 'new',
                    builder: (context, state) => const AmendmentFormPage(),
                  ),
                  GoRoute(
                    path: 'detail',
                    builder: (context, state) => AmendmentDetailPage(amendment: state.extra! as Amendment),
                  ),
                ],
              ),
            ],
          ),
          
          // Branch 4: Profile
          StatefulShellBranch(
            routes: [
              GoRoute(
                path: AppRoutes.profile,
                builder: (context, state) => const ProfilePage(useScaffold: false),
                routes: [
                  GoRoute(
                    path: 'edit',
                    builder: (context, state) => EditProfilePage(profile: state.extra! as UserProfile),
                  ),
                ],
              ),
            ],
          ),
        ],
      ),
    ],
  );
});