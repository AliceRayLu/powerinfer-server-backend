import sys
import base64
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.asymmetric import padding
from cryptography.hazmat.primitives import serialization

def verify_signature(pubkey: str, signature: str, data: str) -> str:
    """
    验证签名是否正确
    :param pubkey: 公钥字符串
    :param signature: Base64编码的签名
    :param data: 原始数据
    :return: 验证结果（True/False）
    """
    try:
        # 加载公钥
        public_key = serialization.load_ssh_public_key(pubkey.encode('utf-8'))

        # 解码Base64签名
        signature_bytes = base64.b64decode(signature)

        # 验证签名
        public_key.verify(
            signature_bytes,
            data.encode('utf-8'),
            padding.PKCS1v15(),
            hashes.SHA256()
        )
        return "True"
    except Exception as e:
        return "False: " + str(e) + ""

if __name__ == "__main__":
    print(verify_signature(sys.argv[1], sys.argv[3], sys.argv[2]))