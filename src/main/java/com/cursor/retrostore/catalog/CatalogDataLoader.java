package com.cursor.retrostore.catalog;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CatalogDataLoader implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CatalogDataLoader(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            return;
        }

        Category keyboards = categoryRepository.save(new Category("keyboards", "Mechanical keyboards", 10));
        Category boards = categoryRepository.save(new Category("motherboards", "Motherboards & CPUs", 20));
        Category storage = categoryRepository.save(new Category("storage", "Floppy & tape", 30));
        Category video = categoryRepository.save(new Category("video", "CRT & GPU", 40));
        Category audio = categoryRepository.save(new Category("audio", "Sound & MIDI", 50));

        productRepository.save(new Product(
                "KB-MODELM-87",
                "IBM Model M (buckling spring)",
                "Classic PS/2 layout. Loud, satisfying, and built like a tank.",
                new BigDecimal("189.00"),
                12,
                keyboards));
        productRepository.save(new Product(
                "KB-ALPS-101",
                "Alps SKCM Cream keyboard",
                "Full-size with genuine Alps switches. Yellowed case sold separately.",
                new BigDecimal("129.50"),
                7,
                keyboards));

        productRepository.save(new Product(
                "MB-AT-286",
                "16-bit ISA motherboard (80286)",
                "Includes socket for 80287 FPU. Battery holder replaced; no leaking barrel.",
                new BigDecimal("245.00"),
                4,
                boards));
        productRepository.save(new Product(
                "CPU-486DX33",
                "Intel 486DX-33 (socket 3)",
                "Vintage DX chip for your restoration build. Tested POST OK.",
                new BigDecimal("89.99"),
                15,
                boards));

        productRepository.save(new Product(
                "FD-35-DD",
                "5.25\" high-density floppy drive",
                "1.2MB DS/HD. Clean heads, tested with known-good diskettes.",
                new BigDecimal("42.00"),
                9,
                storage));
        productRepository.save(new Product(
                "TAPE-QIC80",
                "QIC-80 tape drive + cable",
                "External SCSI-style connector. Great for backups that feel retro.",
                new BigDecimal("67.50"),
                3,
                storage));

        productRepository.save(new Product(
                "CRT-ADP-VGA",
                "VGA to composite adapter",
                "For when your demo needs authentic scanlines on a period-correct display.",
                new BigDecimal("34.00"),
                20,
                video));
        productRepository.save(new Product(
                "GPU-Voodoo2",
                "3dfx Voodoo2 12MB",
                "SLI-ready nostalgia. Glide titles never looked better on a CRT.",
                new BigDecimal("199.00"),
                2,
                video));

        productRepository.save(new Product(
                "SND-SB16",
                "Sound Blaster 16 (ISA)",
                "OPL3 FM + digital audio. IRQ/DMA jumpers intact.",
                new BigDecimal("78.00"),
                6,
                audio));
        productRepository.save(new Product(
                "SND-MT32",
                "Roland MT-32 (MIDI)",
                "LA synthesis for classic game soundtracks. Power brick included.",
                new BigDecimal("320.00"),
                1,
                audio));
    }
}
