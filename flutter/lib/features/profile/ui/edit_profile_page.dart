import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import '../../../core/models/user_profile.dart';
import '../../../core/network/dio_client.dart';
import '../../../core/network/network_exception_utils.dart';

class EditProfilePage extends ConsumerStatefulWidget {
  const EditProfilePage({super.key, required this.profile});

  final UserProfile profile;

  @override
  ConsumerState<EditProfilePage> createState() => _EditProfilePageState();
}

class _EditProfilePageState extends ConsumerState<EditProfilePage> {
  late final TextEditingController _fullNameController;
  late final TextEditingController _emailController;
  late final TextEditingController _addressController;
  late final TextEditingController _personalPhoneController;
  late final TextEditingController _officeExtensionController;

  bool _saving = false;
  String? _errorMsg;

  @override
  void initState() {
    super.initState();
    _fullNameController = TextEditingController(text: widget.profile.fullName);
    _emailController = TextEditingController(text: widget.profile.email);
    _addressController = TextEditingController(text: widget.profile.address ?? '');
    _personalPhoneController = TextEditingController(text: widget.profile.personalPhone ?? '');
    _officeExtensionController = TextEditingController(text: widget.profile.officeExtension ?? '');
  }

  @override
  void dispose() {
    _fullNameController.dispose();
    _emailController.dispose();
    _addressController.dispose();
    _personalPhoneController.dispose();
    _officeExtensionController.dispose();
    super.dispose();
  }

  String? _emptyToNull(String value) {
    final trimmed = value.trim();
    return trimmed.isEmpty ? null : trimmed;
  }

  Future<void> _submit() async {
    setState(() {
      _saving = true;
      _errorMsg = null;
    });

    final body = <String, dynamic>{
      'fullName': _fullNameController.text.trim(),
      'email': _emailController.text.trim(),
      'address': _emptyToNull(_addressController.text),
      'personalPhone': _emptyToNull(_personalPhoneController.text),
      'officeExtension': _emptyToNull(_officeExtensionController.text),
    };

    try {
      final dio = ref.read(dioProvider);
      await dio.put<void>('/api/users/me', data: body);
      if (mounted) {
        context.pop(true);
      }
    } on DioException catch (e) {
      setState(() {
        _errorMsg = extractDioErrorMessage(e, fallback: '更新失敗，請稍後再試');
      });
    } catch (e) {
      setState(() {
        _errorMsg = e.toString();
      });
    } finally {
      if (mounted) {
        setState(() => _saving = false);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('編輯個人資料')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            if (_errorMsg != null)
              Padding(
                padding: const EdgeInsets.only(bottom: 16),
                child: Text(
                  _errorMsg!,
                  style: TextStyle(color: Theme.of(context).colorScheme.error),
                ),
              ),
            TextField(
              controller: _fullNameController,
              decoration: const InputDecoration(labelText: '姓名'),
            ),
            const SizedBox(height: 16),
            TextField(
              controller: _emailController,
              decoration: const InputDecoration(labelText: 'Email'),
              keyboardType: TextInputType.emailAddress,
            ),
            const SizedBox(height: 16),
            TextField(
              controller: _addressController,
              decoration: const InputDecoration(labelText: '地址（選填）'),
            ),
            const SizedBox(height: 16),
            TextField(
              controller: _personalPhoneController,
              decoration: const InputDecoration(labelText: '個人聯絡電話（選填）'),
              keyboardType: TextInputType.phone,
            ),
            const SizedBox(height: 16),
            TextField(
              controller: _officeExtensionController,
              decoration: const InputDecoration(labelText: '公司分機電話（選填）'),
              keyboardType: TextInputType.phone,
            ),
            const SizedBox(height: 32),
            FilledButton(
              onPressed: _saving ? null : _submit,
              child: _saving
                  ? const SizedBox(
                      height: 20,
                      width: 20,
                      child: CircularProgressIndicator(strokeWidth: 2),
                    )
                  : const Text('儲存'),
            ),
          ],
        ),
      ),
    );
  }
}
