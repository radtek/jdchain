package test.com.jd.blockchain.crypto.service.classic;

import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.CryptoException;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.HashFunction;
import com.jd.blockchain.crypto.service.classic.ClassicAlgorithm;
import com.jd.blockchain.utils.io.BytesUtils;
import org.junit.Test;

import java.util.Random;

import static com.jd.blockchain.crypto.CryptoAlgorithm.HASH_ALGORITHM;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * @author zhanglin33
 * @title: RIPEMD160HashFunctionTest
 * @description: JunitTest for RIPEMD160HashFunction in SPI mode
 * @date 2019-04-01, 14:03
 */
public class RIPEMD160HashFunctionTest {

	@Test
	public void getAlgorithmTest() {
		CryptoAlgorithm algorithm = Crypto.getAlgorithm("RIPEMD160");
		assertNotNull(algorithm);

		HashFunction hashFunction = Crypto.getHashFunction(algorithm);

		assertEquals(hashFunction.getAlgorithm().name(), algorithm.name());
		assertEquals(hashFunction.getAlgorithm().code(), algorithm.code());

		algorithm = Crypto.getAlgorithm("RIPEmd160");
		assertNotNull(algorithm);

		assertEquals(hashFunction.getAlgorithm().name(), algorithm.name());
		assertEquals(hashFunction.getAlgorithm().code(), algorithm.code());

		algorithm = Crypto.getAlgorithm("RIPEMD-160");
		assertNull(algorithm);
	}

	@Test
	public void hashTest() {

		byte[] data = new byte[1024];
		Random random = new Random();
		random.nextBytes(data);

		CryptoAlgorithm algorithm = Crypto.getAlgorithm("RIPEMD160");
		assertNotNull(algorithm);

		HashFunction hashFunction = Crypto.getHashFunction(algorithm);

		HashDigest digest = hashFunction.hash(data);
		byte[] rawDigestBytes = digest.getRawDigest();
		byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);

		byte[] digestBytes = digest.toBytes();
		assertEquals(160 / 8 + 2, digestBytes.length);
		assertArrayEquals(digestBytes, BytesUtils.concat(algoBytes, rawDigestBytes));

		assertEquals(algorithm.code(), digest.getAlgorithm());

		Class<?> expectedException = CryptoException.class;
		Exception actualEx = null;
		try {
			data = null;
			hashFunction.hash(data);
		} catch (Exception e) {
			actualEx = e;
		}
		assertNotNull(actualEx);
		assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
	}

	@Test
	public void verifyTest() {
		byte[] data = new byte[1024];
		Random random = new Random();
		random.nextBytes(data);

		CryptoAlgorithm algorithm = Crypto.getAlgorithm("RIPEMD160");
		assertNotNull(algorithm);

		HashFunction hashFunction = Crypto.getHashFunction(algorithm);

		HashDigest digest = hashFunction.hash(data);

		assertTrue(hashFunction.verify(digest, data));
	}

	@Test
	public void supportHashDigestTest() {
		byte[] data = new byte[1024];
		Random random = new Random();
		random.nextBytes(data);

		CryptoAlgorithm algorithm = Crypto.getAlgorithm("RIPEMD160");
		assertNotNull(algorithm);

		HashFunction hashFunction = Crypto.getHashFunction(algorithm);

		HashDigest digest = hashFunction.hash(data);

		byte[] digestBytes = digest.toBytes();
		assertTrue(hashFunction.supportHashDigest(digestBytes));

		algorithm = Crypto.getAlgorithm("aes");
		assertNotNull(algorithm);
		byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
		System.arraycopy(algoBytes, 0, digestBytes, 0, algoBytes.length);
		assertFalse(hashFunction.supportHashDigest(digestBytes));
	}

	@Test
	public void resolveHashDigestTest() {
		byte[] data = new byte[1024];
		Random random = new Random();
		random.nextBytes(data);

		CryptoAlgorithm algorithm = Crypto.getAlgorithm("RIPEMD160");
		assertNotNull(algorithm);

		HashFunction hashFunction = Crypto.getHashFunction(algorithm);

		HashDigest digest = hashFunction.hash(data);

		byte[] digestBytes = digest.toBytes();

		HashDigest resolvedDigest = hashFunction.resolveHashDigest(digestBytes);

		assertEquals(160 / 8, resolvedDigest.getRawDigest().length);
		assertEquals(ClassicAlgorithm.RIPEMD160.code(), resolvedDigest.getAlgorithm());
		assertEquals((short) (HASH_ALGORITHM | ((byte) 25 & 0x00FF)), resolvedDigest.getAlgorithm());
		assertArrayEquals(digestBytes, resolvedDigest.toBytes());

		algorithm = Crypto.getAlgorithm("aes");
		assertNotNull(algorithm);
		byte[] algoBytes = CryptoAlgorithm.toBytes(algorithm);
		byte[] rawDigestBytes = digest.getRawDigest();
		byte[] aesDigestBytes = BytesUtils.concat(algoBytes, rawDigestBytes);

		Class<?> expectedException = CryptoException.class;
		Exception actualEx = null;
		try {
			hashFunction.resolveHashDigest(aesDigestBytes);
		} catch (Exception e) {
			actualEx = e;
		}
		assertNotNull(actualEx);
		assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));

		algorithm = Crypto.getAlgorithm("sha256");
		assertNotNull(algorithm);
		algoBytes = CryptoAlgorithm.toBytes(algorithm);
		rawDigestBytes = digest.getRawDigest();
		byte[] ripemd160DigestBytes = BytesUtils.concat(algoBytes, rawDigestBytes);

		expectedException = CryptoException.class;
		actualEx = null;
		try {
			hashFunction.resolveHashDigest(ripemd160DigestBytes);
		} catch (Exception e) {
			actualEx = e;
		}
		assertNotNull(actualEx);
		assertTrue(expectedException.isAssignableFrom(actualEx.getClass()));
	}
}
