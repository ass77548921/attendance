import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../../../core/models/amendment.dart';
import '../data/amendment_repository.dart';

final amendmentsProvider = FutureProvider.autoDispose<List<Amendment>>((ref) async {
  return ref.watch(amendmentRepositoryProvider).getMyAmendments();
});

final amendmentStatusFilterProvider = StateProvider<AmendmentStatus?>((ref) => null);